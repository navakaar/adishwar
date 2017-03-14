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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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


/**
 * Created by dhirb on 28-02-2017.
 */

public class MyAdi_GreetingsActivity extends Activity {
    private static final String TAG = "MyAdi_GreetingsActivity";
    private static final int CircularTextView_ID = 9000000;
    Employee[] employeeList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadi_greetings);

        setScreenFonts();

        // Make a call to the background Async task
        // for seeking birthdays in current month
        new BirthdaysThisMonthTask().execute();
    }


    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        // Set Font
        TextView greetHeader_Tv = (TextView) findViewById(R.id.myag_greet_Tv);
        greetHeader_Tv.setTypeface(typeface, Typeface.BOLD_ITALIC);  greetHeader_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
    }

    // Background Task to Retrieve list of people who's birthday falls in this month
    // // GYAAN:: An asynchronous task is defined by 3 generic types, called Params, Progress and Result, and
    // // 4 steps, called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
    // // // Params, the type of the parameters sent to the task upon execution
    // // // Progress, the type of the progress units published during the background computation.
    // // // Result, the type of the result of the background computation.
    private class BirthdaysThisMonthTask extends AsyncTask<Void, Integer, String> {
        ProgressBar progressbar;
        int currMonth;


        @Override
        protected void onPreExecute() {
            progressbar = (ProgressBar)findViewById(R.id.myag_progress_Pb);
            progressbar.setVisibility(View.VISIBLE);

            // Get Current Month
            java.util.Date date= new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            currMonth = cal.get(Calendar.MONTH)+1;

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/GetBirthdaysThisMonth.php");


                //
                // Send POST Request
                //
                JSONObject birthListInputJSON = new JSONObject();
                birthListInputJSON.put("month_of_interest", currMonth);
                Log.d(getClass().getName(), "JSON to be Sent === " + birthListInputJSON.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(birthListInputJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(birthListInputJSON.toString().getBytes());

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
                        return "ERROR | Step2 | Some Error Occurred in GetBirthdaysThisMonth.php call! (Blank Response)";
                    }
                    else
                        Log.d(getClass().getName(), "Response JSON == " + sb.toString());


                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());

                        if (responseJson.getString("error_status").equals("true")) // Status (error == true) received
                        {
                            return "ERROR | Step3 | Some Error Occurred in GetBirthdaysThisMonth.php call!";
                        } else if (responseJson.getString("error_status").equals("false") && responseJson.getInt("birthday_match_count")==0) {
                            return "SUCCESS | Birthday Match = 0"; //NO Birthdays Matched for given month
                        } else { // retrieve matched products
                            JSONArray jsonProdList = responseJson.getJSONArray("birthdays_matched");
                            int birthdaycount = jsonProdList.length();

                            // initialize array with given count
                            employeeList = new Employee[birthdaycount];

                            for (int i=0; i<birthdaycount; i++) {
                                JSONObject jsonProduct = jsonProdList.getJSONObject(i);
                                Employee emp = new Employee(jsonProduct.getString("name"), jsonProduct.getString("phone"),
                                                                jsonProduct.getString("store"), jsonProduct.getString("DOB"), jsonProduct.getString("DOA"));

                                emp.print();
                                employeeList[i] = emp;
                            }
                            return "SUCCESS | Birthday Match = " + birthdaycount;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | Step1 | Some Error Occured in GetBirthdaysThisMonth.php call! (" + responseCode + ")");
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

            return new String("ERROR | Step0 | Some Issue in GetBirthdaysThisMonth");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
           progressbar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String response) {
            progressbar.setVisibility(View.GONE);

            Log.d(getClass().getName(), "Response == " + response);

            if (response.contains("SUCCESS | Birthday Match = 0")) {    // No employee birthdays this month
                Toast.makeText(MyAdi_GreetingsActivity.this, "No Employee Birthdays This Month!", Toast.LENGTH_LONG).show();
            } else if (response.contains("SUCCESS | Birthday Match =")) {
                for (int i=0; i<employeeList.length;i++) {
                    CircularTextView newBakraCTv = new CircularTextView(MyAdi_GreetingsActivity.this);
                    int day = 0, month = 0, year = 0;
                    try {
                        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(employeeList[i].dob);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(dob);
                        month = cal.get(Calendar.MONTH) + 1;    // Calendear Month is 0-based hence incrementing by 1
                        day = cal.get(Calendar.DAY_OF_MONTH);
                        year = cal.get(Calendar.YEAR);
                        //Log.d(TAG, i+ ".>>DOB>> day = " + day + ", Mth = " + month +", Yr = " + year);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    newBakraCTv.setText( employeeList[i].name + " (" + day + "/" + month + ")\n" + employeeList[i].store );
                    newBakraCTv.setId(CircularTextView_ID+i);

                    // Set Circle color
                    newBakraCTv.setStrokeWidth(2);
                    newBakraCTv.setStrokeColor("#ffffff");
                    if ((i % 2) == 0) {
                        newBakraCTv.setSolidColor("#FFE332");
                    } else
                        newBakraCTv.setSolidColor("#c6ff00");

                    // Set Text font, color
                    Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                            String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
                    newBakraCTv.setTypeface(typeface2, Typeface.BOLD); newBakraCTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

                    // Set position
                    newBakraCTv.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
                    // Set Margins
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 10, 0, 2);
                    newBakraCTv.setLayoutParams(params);
                    // Set padding
                    newBakraCTv.setPadding(8, 0, 8, 0);
                    newBakraCTv.setMaxLines(2);

                    // Add to LinearLayout at 'i' position
                    LinearLayout birthdayBakraLL = (LinearLayout)findViewById(R.id.myag_birthdaybakra_Ll);
                    birthdayBakraLL.addView(newBakraCTv, i);

                    // OnClick
                    newBakraCTv.setClickable(true);
                    newBakraCTv.callOnClick();

                    newBakraCTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendWishes(v.getId());
                        }
                    });


                    Log.d(TAG, "New CircularTextView Added, with ID = " + newBakraCTv.getId());
                }
            } else
                Toast.makeText(MyAdi_GreetingsActivity.this, "[Error] Some Error Occurred!", Toast.LENGTH_LONG).show();
        }

        void sendWishes(int viewID) {
            int empIndex = viewID - CircularTextView_ID;
            String number = employeeList[empIndex].phone;
            String name = employeeList[empIndex].name;

            Log.d(TAG, "SendWishes to: " + number);

            /*Uri uri = Uri.parse("smsto:" + number);
            Intent i = new Intent(Intent.ACTION_SENDTO, uri);
            i.setPackage("com.whatsapp");
            startActivity(Intent.createChooser(i, "Send your wishes over WhatsApp?"));
            */

            Uri uri = Uri.parse("smsto:" + number);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", "Hi " + name + "! Wishing you a very Happy Birthday. God Bless!");
            //intent.putExtra(Intent.EXTRA_TEXT, "I'm the body.");
            startActivity(Intent.createChooser(intent, "Want to send your wishes to " + name + " ?"));
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            //sendIntent.putExtra("sms_body", "Hi! Wishing you a very Happy Birthday. God Bless!");
            sendIntent.setData(Uri.parse("sms:"));
        }
    }
}

