package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.input;

/**
 * Created by dhiraj on 11-Jan-17.
 */

public class SalesLogActivity extends Activity {
    private EditText cusName_Et = null, cphone_Et = null, prodCod_Et = null, billno_Et = null, billamt_Et = null;
    TextView billDt_Tv = null;
    private Button submit_Btn = null;
    private Calendar calendar = null;

    ProgressBar pb = null;

    String customerName = "", customerPhone = "", prodCodes = "";
    int billNo = 0, billAmt = 0;
    private int year = 0, month = 0, day = 0;
    private StringBuilder currentDateSB;

    private SalesCredit salesCredit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_log);

        // Set Font
        setScreenFonts();

        // Set current date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR); month = calendar.get(Calendar.MONTH); day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        // Date change listener
        billDt_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        // Hide progress bar
        pb = (ProgressBar)findViewById(R.id.asl_progress_Pb);
        pb.setVisibility(View.GONE);

        // Validate Inputs
        submit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateInputs())
                    return;
                captureInputs();

                // Call Async task to save inputs to the DB
                // Make Progress bar visible
                pb.setVisibility(View.VISIBLE);
                // These parameters to be sent to the DB for retrieving product details (description, price, offers)
                new SalesLogTask().execute(salesCredit);

            }
        });
    }

    void captureInputs() {
        salesCredit = new SalesCredit(customerName, customerPhone, prodCodes, billNo, billAmt, 1, currentDateSB.toString());
    }

    boolean validateInputs() {

        // Customer Name cannot be blank
        customerName = cusName_Et.getText().toString().trim();
        if (customerName.length() == 0) {
            Toast.makeText(getApplicationContext(), "[ERROR] Enter Customer Name", Toast.LENGTH_LONG).show();
            return false;
        }

        // Phone Number cannot be blank or less than 10 digits
        customerPhone = cphone_Et.getText().toString().trim();
        if (customerPhone.length() == 0 || customerPhone.trim().length()<10) {
            Toast.makeText(getApplicationContext(), "[ERROR] Enter a 10-digit Mobile Number", Toast.LENGTH_LONG).show();
            return false;
        }
        // Phone number cannot contain any special characters, only digits allowed
        Pattern pattern = Pattern.compile("[*+#()/N-]");
        Matcher matcher = pattern.matcher(customerPhone);
        if (matcher.find()) {
            // contains special characters
            Toast.makeText(getApplicationContext(), "[ERROR] Enter numerical digits only, No other characters allowed!", Toast.LENGTH_LONG).show();
            return false;
        }

        // Product Code cannot be blank or less than 4 characters
        prodCodes = prodCod_Et.getText().toString().trim();
        if (prodCodes.length() == 0 || prodCodes.length()<4) {
            Toast.makeText(getApplicationContext(), "[ERROR] Enter valid product code", Toast.LENGTH_LONG).show();
            return false;
        }

        // Bill Number cannot be blank
        String billNoStr = billno_Et.getText().toString().trim();
        if (billNoStr.length() == 0) {
            Toast.makeText(getApplicationContext(), "[ERROR] Enter Bill No.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            billNo = Integer.parseInt(billNoStr);
        }

        // Bill Amount cannot be blank
        String billAmtStr = billamt_Et.getText().toString().trim();
        if (billAmtStr.length() == 0) {
            Toast.makeText(getApplicationContext(), "[ERROR] Enter Bill Amount", Toast.LENGTH_LONG).show();
            return false;
        }
        billAmt = Integer.parseInt(billAmtStr);

        return true;
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView salesLog_Tv = (TextView)findViewById(R.id.asl_sales_log_Tv);
        salesLog_Tv.setTypeface(typeface, Typeface.BOLD); salesLog_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView cusDetails_Tv = (TextView)findViewById(R.id.asl_cusdetails_Tv);
        cusDetails_Tv.setTypeface(typeface2, Typeface.BOLD); cusDetails_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView cusName_Tv = (TextView)findViewById(R.id.asl_cname_Tv);
        cusName_Et = (EditText)findViewById(R.id.asl_cname_Et);
        cusName_Tv.setTypeface(typeface2); cusName_Et.setTypeface(typeface2);

        TextView cphone_Tv = (TextView)findViewById(R.id.asl_cphone_Tv);
        cphone_Et = (EditText)findViewById(R.id.asl_cphone_Et);
        cphone_Tv.setTypeface(typeface2); cphone_Et.setTypeface(typeface2);

        TextView prodsold_Tv = (TextView)findViewById(R.id.asl_prodSold_Tv);
        prodsold_Tv.setTypeface(typeface2, Typeface.BOLD); prodsold_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView prodCod_Tv = (TextView)findViewById(R.id.asl_prodcod_Tv);
        prodCod_Et = (EditText)findViewById(R.id.asl_prodcod_Et);
        prodCod_Tv.setTypeface(typeface2); prodCod_Et.setTypeface(typeface2);


        TextView billdetails_Tv = (TextView)findViewById(R.id.asl_billdetails_Tv);
        billdetails_Tv.setTypeface(typeface2, Typeface.BOLD); billdetails_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        TextView billno_Tv = (TextView)findViewById(R.id.asl_billno_Tv);
        billno_Et = (EditText)findViewById(R.id.asl_billno_Et);
        billno_Tv.setTypeface(typeface2); billno_Et.setTypeface(typeface2);

        TextView billDtt_Tv = (TextView)findViewById(R.id.asl_billdtt_Tv);
        billDt_Tv = (TextView) findViewById(R.id.asl_billdt_Tv);
        billDtt_Tv.setTypeface(typeface2); billDt_Tv.setTypeface(typeface2);

        TextView billamt_Tv = (TextView)findViewById(R.id.asl_billamt_Tv);
        billamt_Et = (EditText)findViewById(R.id.asl_billamt_Et);
        billamt_Tv.setTypeface(typeface2);
        billamt_Et.setTypeface(typeface2); billamt_Et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);

        submit_Btn = (Button)findViewById(R.id.asl_submit_Btn);
        submit_Btn.setTypeface(typeface2);

        return;
    }

    private void showDate(int y, int m, int d) {
        currentDateSB = new StringBuilder().append(d).append("/").append(m).append("/").append(y);
        billDt_Tv.setText(currentDateSB);
    }

    public void setDate(){
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Select Sale Date", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yr, int mth, int dayOfMonth) {
            showDate(yr, mth+1, dayOfMonth);
        }
    };

    private class SalesLogTask extends AsyncTask<SalesCredit, Integer, String> {
        ProgressBar salesLog_progressbar;

        @Override
        protected void onPreExecute() {
            salesLog_progressbar = (ProgressBar) findViewById(R.id.asl_progress_Pb);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(SalesCredit... params) {

            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/SalesLog.php");


                //
                // Send POST Request
                //
                JSONObject salesCreditJSON = new JSONObject();
                salesCreditJSON.put("salescredit", SalesCredit.toMap(params[0]));
                Log.d(getClass().getName(), "JSON to be Sent === " + salesCreditJSON.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(salesCreditJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(salesCreditJSON.toString().getBytes());

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
                        return "ERROR | Error in SalesCredit Logging! | <Blank Response>";
                    }

                    Log.d(getClass().getName(), "Response JSON == " + sb.toString());


                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());

                        if (responseJson.getString("error_status").equals("true") && responseJson.getString("SalesLogStatus").length()>0) // Status (error == true) received
                        {
                            return "ERROR | Log Status = " + responseJson.getString("SalesLogStatus");
                        } else if (responseJson.getString("error_status").equals("false") && responseJson.getString("SalesLogStatus").equals("Sales Logged Successfully")) {
                            return "SUCCESS | SalesCredit Log Completed";
                        } else
                            return "ERROR | error_status missing!";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | HTTP Error in SalesCredit Logging! (" + responseCode + ")");
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

            return "ERROR : SalesCredit logging has failed somewhere";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            salesLog_progressbar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String response) {
            salesLog_progressbar.setVisibility(View.GONE);
            Log.d(getClass().getName(), "Response == " + response);


            if (response.contains("SUCCESS")) {
                Toast.makeText(SalesLogActivity.this, "[SUCCESS] SalesCredit Log Completed!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            } else
                Toast.makeText(SalesLogActivity.this, "[ERROR] SalesCredit not logged! Please retry", Toast.LENGTH_LONG).show();
        }
    }
}