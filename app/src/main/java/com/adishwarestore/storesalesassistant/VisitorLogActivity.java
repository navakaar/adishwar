package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhiraj on 09-Jan-17.
 */

public class VisitorLogActivity extends Activity implements VisitorLogVDetailsFragment.OnClientDetailsNextButtonClickedInterface,
                                                                VisitorLogPDetailsFragment.OnProductDetailsNextButtonClickedInterface,
                                                                VisitorLogBuyInfoFragment.OnBuyInfoNextButtonClickedInterface {
    private EditText visitorName_Et = null, visitorPh_Et = null, prodCod_Et = null;
    private Button submit_Btn = null;
    private RadioButton ceRadio = null, caRadio = null, dapRadio = null, othRadio = null;
    RadioGroup buyTimeFmRG = null;
    private RadioButton stRadio = null, mdRadio = null, lgpRadio = null;
    private Spinner prodListSpinner = null;
    ProgressBar pb = null;

    private String visitorName = "", visitorPhone = "", prodCode = "";
    private Visitor visitor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitorlog);

        setScreenFonts();


        // Replace the first fragment into the fragment_container placeholder
        if (findViewById(R.id.avl_fragment_container) != null) {

            Fragment vDetailsFragment = new VisitorLogVDetailsFragment();
            vDetailsFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            //getFragmentManager().beginTransaction().add(R.id.fragment_container, productSearchFragment, "SearchProduct").addToBackStack("SearchProduct").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            // // GYAAN: If you have more than one fragment been used in the activity or even if you have only one
            // // fragment then the first fragment should not have addToBackStack defined. Since this allows back navigation
            // // and prior to this fragment the empty activity layout will be displayed.
            // // fragmentmaganer.addToBackStack() // dont include this for your first fragment.
            getFragmentManager().beginTransaction().add(R.id.avl_fragment_container, vDetailsFragment, "VisitorLogVDetailsFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }

        // Hide progress bar
        pb = (ProgressBar)findViewById(R.id.avl_progress_Pb);
        pb.setVisibility(View.GONE);
    }

    @Override
    public void OnBuyInfoNextButtonClicked(Visitor v) {
        //Toast.makeText(getApplicationContext(), "[LOG] Visitor data submitted to HeadOffice", Toast.LENGTH_LONG).show();

        // Call Async task to save inputs to the DB
        // Make Progress bar visible
        pb.setVisibility(View.VISIBLE);
        // These parameters to be sent to the DB for retrieving product details (description, price, offers)
        new VisitorLogTask().execute(v);
    }

    @Override
    public void OnProductDetailsNextButtonClicked(Visitor v) {
        // Show Buy Info
        VisitorLogBuyInfoFragment visitorLogBuyInfoFragment = new VisitorLogBuyInfoFragment();
        visitorLogBuyInfoFragment.setArguments(getIntent().getExtras());
        getIntent().putExtra("visitor", v);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.avl_fragment_container, visitorLogBuyInfoFragment, "VisitorLogBuyInfoFragment").addToBackStack("VisitorLogBuyInfoFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    @Override
    public void OnClientDetailsNextButtonClicked(Visitor v) {
        // Show product details
        VisitorLogPDetailsFragment visitorLogPDetailsFragment = new VisitorLogPDetailsFragment();
        visitorLogPDetailsFragment.setArguments(getIntent().getExtras());
        getIntent().putExtra("visitor", v);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.avl_fragment_container, visitorLogPDetailsFragment, "VisitorLogPDetailsFragment").addToBackStack("VisitorLogPDetailsFragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView vistorLog_Tv = (TextView) findViewById(R.id.avl_visitor_log_Tv);
        vistorLog_Tv.setTypeface(typeface, Typeface.BOLD);
        vistorLog_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        return;
    }


    private class VisitorLogTask extends AsyncTask<Visitor, Integer, String> {
        ProgressBar visitLog_progressbar;

        @Override
        protected void onPreExecute() {
            visitLog_progressbar = (ProgressBar) findViewById(R.id.avl_progress_Pb);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Visitor... params) {

            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/VisitorLog.php");


                //
                // Send POST Request
                //
                JSONObject visitorJSON = new JSONObject();
                visitorJSON.put("visitor", Visitor.toMap(params[0]));
                Log.d(getClass().getName(), "JSON to be Sent === " + visitorJSON.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(visitorJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(visitorJSON.toString().getBytes());

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
                        return "ERROR | Error in Visitor Logging! | <Blank Response>";
                    }

                    //Log.d(getClass().getName(), "Response JSON == " + sb.toString());


                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());

                        if (responseJson.getString("error_status").equals("true") && responseJson.getString("VisitorLogStatus").length()>0) // Status (error == true) received
                        {
                            return "ERROR | Log Status = " + responseJson.getString("VisitorLogStatus");
                        } else if (responseJson.getString("error_status").equals("false") && responseJson.getString("VisitorLogStatus").equals("Visitor Logged Successfully")) {
                            return "SUCCESS | Visitor Log Completed";
                        } else
                            return "ERROR | error_status missing!";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | HTTP Error in Visitor Logging! (" + responseCode + ")");
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

            return "ERROR : Visitor logging has failed somewhere";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            visitLog_progressbar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String response) {
            visitLog_progressbar.setVisibility(View.GONE);
            Log.d(getClass().getName(), "Response == " + response);


            if (response.contains("SUCCESS")) {
                Toast.makeText(VisitorLogActivity.this, "[SUCCESS] Visitor Log Completed!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(myIntent);
            } else
                Toast.makeText(VisitorLogActivity.this, "[ERROR] Visitor not logged! Please retry", Toast.LENGTH_LONG).show();
        }
    }
}
