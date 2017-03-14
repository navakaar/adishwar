package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.progress;

/**
 * Created by dhirb on 10-03-2017.
 */

public class MyAdi_EmpRecognition extends Activity {
    private static final String TAG = "MyAdi_EmpRecognition";
    TextView emp1_Tv, emp2_Tv;
    EmpAward[] empAwardList = null;
    private static final int EmpAwardView_ID = 9100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadi_emprecognition);

        setScreenFonts();

        new GetEmpAwardsTask().execute();
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "allura_regular.ttf"));
        Typeface typeface3 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        // Set Font
        TextView empRecogHeader_Tv = (TextView) findViewById(R.id.myer_emprcog_Tv);
        empRecogHeader_Tv.setTypeface(typeface, Typeface.BOLD_ITALIC);  empRecogHeader_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView mdMsg_Tv = (TextView) findViewById(R.id.myer_mdMsg_Tv);
        mdMsg_Tv.setTypeface(typeface2, Typeface.BOLD_ITALIC);

        emp1_Tv = (TextView) findViewById(R.id.myer_emp1_Tv);
        emp1_Tv.setTypeface(typeface3);

        emp2_Tv = (TextView) findViewById(R.id.myer_emp2_Tv);
        emp2_Tv.setTypeface(typeface3);
    }

    private class GetEmpAwardsTask extends AsyncTask<Integer, Integer, String> {
        ProgressBar progressbar;
        int currMonth;

        @Override
        protected void onPreExecute() {
            progressbar = (ProgressBar)findViewById(R.id.myer_progress_Pb);
            progressbar.setVisibility(View.VISIBLE);

            // Get Current Month
            java.util.Date date= new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currMonth = cal.get(Calendar.MONTH)+1;

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/GetEmpAwardsThisMonth.php");


                //
                // Send POST Request
                //
                JSONObject inputJSON = new JSONObject();
                inputJSON.put("month_of_interest", currMonth);
                Log.d(getClass().getName(), "JSON to be Sent === " + inputJSON.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(inputJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(inputJSON.toString().getBytes());

                outputStream.flush();
                outputStream.close();

                //
                // Read HTTP Response
                //
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    inputStream = urlConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    bufferedReader.close();
                    //return sb.toString();

                    if (sb.length() == 0) {
                        // Stream was empty. No point in parsing.
                        return "ERROR | Step2 | Some Error Occurred in GetEmpAwardsThisMonth.php call! (Blank Response)";
                    }
                    else
                        Log.d(getClass().getName(), "Response JSON == " + sb.toString());


                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());

                        if (responseJson.getString("error_status").equals("true")) // Status (error == true) received
                        {
                            return "ERROR | Step3 | Some Error Occurred in GetEmpAwardsThisMonth.php call!";
                        } else if (responseJson.getString("error_status").equals("false") && responseJson.getInt("award_match_count")==0) {
                            return "SUCCESS | Award Match = 0"; //NO Awards Matched for given month
                        } else { // retrieve matched products
                            JSONArray jsonProdList = responseJson.getJSONArray("awards_matched");
                            int awardcount = jsonProdList.length();

                            // initialize array with given count
                            empAwardList = new EmpAward[awardcount];

                            for (int i=0; i<awardcount; i++) {
                                JSONObject jsonProduct = jsonProdList.getJSONObject(i);
                                EmpAward empAward = new EmpAward(jsonProduct.getString("empid"), jsonProduct.getString("name"), jsonProduct.getString("store"),
                                        jsonProduct.getString("awardname"), jsonProduct.getString("awardgivenby"), jsonProduct.getString("awarddate"));

                                empAward.print();
                                empAwardList[i] = empAward;
                            }
                            return "SUCCESS | Award Match = " + awardcount;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | Step1 | Some Error Occured in GetEmpAwardsThisMonth.php call! (" + responseCode + ")");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }

            return new String("ERROR | Step0 | Some Issue in GetEmpAwardsThisMonth");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressbar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            progressbar.setVisibility(View.GONE);

            Log.d(getClass().getName(), "Response == " + response);

            if (response.contains("SUCCESS | Award Match = 0")) {    // No employee awards this month
                Toast.makeText(MyAdi_EmpRecognition.this, "No Employee Awards This Month!", Toast.LENGTH_LONG).show();
            } else if (response.contains("SUCCESS | Award Match =")) {

                //Toast.makeText(MyAdi_EmpRecognition.this, "No. of Employee Awards This Month = " + empAwardList.length, Toast.LENGTH_LONG).show();

                for (int i=0; i<2;i++) {

                    // Set Recognized Employee Name
                    if (i == 0) {
                        emp1_Tv.setText(empAwardList[i].emp_name + "\n" + empAwardList[i].emp_store);
                    } else if (i == 1) {
                        emp2_Tv.setText(empAwardList[i].emp_name + "\n" + empAwardList[i].emp_store);
                    }

                    // Set Recognized Employee Pic
                    UrlImageView empUrlImageView = null;
                    if (i == 0) {
                        empUrlImageView = (UrlImageView)findViewById(R.id.myer_emp1_UIv);
                    } else if (i == 1) {
                        empUrlImageView = (UrlImageView)findViewById(R.id.myer_emp2_UIv);
                    }

                    String imgURLStr = "http://navkar.bitnamiapp.com/Adishwar/images/EMPLOYEE/"
                            + empAwardList[i].emp_id + ".png";
                    //"http://navkar.bitnamiapp.com/Adishwar/images/EMPLOYEE/9886677814.jpg"


                    try {
                        URL empImgUrl = new URL(imgURLStr);
                        Log.d(TAG, "ImgURLStr = " + empImgUrl.toString());
                        empUrlImageView.setImageURL(empImgUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                    /*
                    // Show Employee Pic
                    UrlImageView empUrlImageView = new UrlImageView(MyAdi_EmpRecognition.this);
                    empUrlImageView.setId(EmpAwardView_ID+i);

                    // Employee Image URL
                    String imgURLStr = "http://navkar.bitnamiapp.com/Adishwar/images/EMPLOYEE/"
                                            + empAwardList[i].emp_id + ".png";
                    //"http://navkar.bitnamiapp.com/Adishwar/images/EMPLOYEE/9886677814.jpg"

                    try {
                        URL empImgUrl = new URL(imgURLStr);
                        Log.d("EmpRecognition", "ImgURLStr = " + empImgUrl.toString());
                        empUrlImageView.setImageURL(empImgUrl);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    // Set ImageView Properties
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 5, 0, 2);
                    if (empUrlImageView.getId() > EmpAwardView_ID) {    // leave out the first image
                        params.addRule(RelativeLayout.BELOW, empUrlImageView.getId()-1);
                    }
                    empUrlImageView.setLayoutParams(params);


                    // Set position
                    empUrlImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    empUrlImageView.setScaleType(ImageView.ScaleType.CENTER);
                    empUrlImageView.setMaxWidth(200);
                    empUrlImageView.setMaxHeight(100);
                    empUrlImageView.setPadding(5, 0, 5, 0);
                    // Add to LinearLayout at 'i' position
                    RelativeLayout awardListRL = (RelativeLayout)findViewById(R.id.myer_awardList_Rl);
                    awardListRL.addView(empUrlImageView, i, params);






                    TextView awardeeTv = new TextView(MyAdi_EmpRecognition.this);
                    int day = 0, month = 0, year = 0;

                    awardeeTv.setText( empAwardList[i].emp_name + "\n(" + empAwardList[i].emp_store  + ")");
                    awardeeTv.setId(EmpAwardView_ID+i);

                    // Set Text font, color
                    Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                            String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
                    awardeeTv.setTypeface(typeface2, Typeface.BOLD); awardeeTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                    // Set position
                    awardeeTv.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
                    // Set Margins
                    //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(0, 10, 0, 2);
                    params2.addRule(RelativeLayout.BELOW, awardeeTv.getId()-1);
                    awardeeTv.setLayoutParams(params2);
                    // Set padding
                    awardeeTv.setPadding(8, 0, 8, 0);
                    awardeeTv.setMaxLines(2);

                    // Add to LinearLayout at 'i' position
                    awardListRL.addView(awardeeTv, i+1, params2);

                    // OnClick
                    awardeeTv.setClickable(true);
                    awardeeTv.callOnClick();

                    awardeeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendWishes(v.getId());
                        }
                    });


                    Log.d(TAG, "New TextView Added, with ID = " + awardeeTv.getId());*/
                }
            } else
                Toast.makeText(MyAdi_EmpRecognition.this, "[Error] Some Error Occurred!", Toast.LENGTH_LONG).show();
        }

        void sendWishes(int viewID) {
            int empIndex = viewID - EmpAwardView_ID;
            String number = empAwardList[empIndex].emp_id;
            String name = empAwardList[empIndex].emp_name;

            Log.d(TAG, "SendWishes to: " + number);

            /*Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, "Send your wishes over WhatsApp?"));
            */

            Uri uri = Uri.parse("smsto:" + number);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "Hi " + name + "! Congratulations to you on your great performance! God Bless.");
            //intent.putExtra(Intent.EXTRA_TEXT, "I'm the body.");
            startActivity(Intent.createChooser(intent, "Want to send your wishes to " + name + " ?"));
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            //sendIntent.putExtra("sms_body", "Hi! Wishing you a very Happy Birthday. God Bless!");
            sendIntent.setData(Uri.parse("sms:"));
        }
    }
}
