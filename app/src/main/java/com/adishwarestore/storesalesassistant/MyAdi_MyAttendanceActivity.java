package com.adishwarestore.storesalesassistant;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.location.GpsStatus.GPS_EVENT_STARTED;
import static android.location.GpsStatus.GPS_EVENT_STOPPED;


/**
 * Created by dhirb on 17-02-2017.
 */

public class MyAdi_MyAttendanceActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    protected static final String TAG = "MyAdi_MyAttendanceAct";
    private static final int MyQRScanAtivity_REQUEST = 100; // Request code for the MyAdi_MyAtt_QRScanActivity call
    private boolean gpsEnabled = false;
    private boolean qrCodeScanSuccess = false;
    private boolean locPermissionGranted = false;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    protected TextView empnameTv, datetimeTv, locTv, storeQRInfoTv, errStatusTv;

    Button scanQRBtn, inBtn, outBtn;

    // Attendance data
    private String empId, storeLocation, punchTimeStamp_AppFmt, punchTimeStamp_SQLFmt;
    //private Date currDateTime;
    private double loc_lat, loc_lng;

    // Provides the entry point to Google Play services
    GoogleApiClient mGoogleApiClient;
    // Represents a geographical location
    protected Location mLastLocation;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 5 * 1000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2 * 1000; /* 2 secs */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadi_myattendance);

        setScreenFonts();

        // Set Loggedin Employee Name
        empId = "8800011222";   // get this from main activity
        empnameTv.setText(empnameTv.getText() + "Keshav Kumar"); // get this from main activity

        // Get formatted current date-time
        Calendar c = Calendar.getInstance();
        Date currDateTime = c.getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        punchTimeStamp_AppFmt = df1.format(currDateTime);   // for showing in the APP
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        punchTimeStamp_SQLFmt = df2.format(currDateTime);   // for saving in the SQL DB
        // Set current date-time
        datetimeTv.setText(datetimeTv.getText()+ punchTimeStamp_AppFmt);


        if (!isNetworkAvailable()) {         // Check that the phone is connected to the data network
            Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Error] Your Phone data connection is NOT ON; Please enable data (2g/3g/4g/wifi)", Toast.LENGTH_LONG).show();
            finish();
        }

        // Check/Ask for location access for the App (required for Attendance feature)


        // Check whether App has been granted permission to access location
        checkLocationPermissionGranted(); // required in API 23 & greater
        if (!locPermissionGranted) {
            //Toast.makeText(this, "You have not granted this App location access, so unable to punch attendance!!", Toast.LENGTH_LONG).show();
            locTv.setText("Unable to detect location!");
            locTv.setTextColor(Color.RED);
            errStatusTv.setText("App does not have permission to access device Location!\nGo to \"Settings > Application Manager > 'Store Sales Assistant' > Permissions > Location\" (enable)");
            errStatusTv.setVisibility(View.VISIBLE);
        } else {
            errStatusTv.setVisibility(View.INVISIBLE);
        }
        // Button listeners
        scanQRBtn.setOnClickListener(this); inBtn.setOnClickListener(this); outBtn.setOnClickListener(this);

        // Create an instance of GoogleAPIClient for location access
        buildGoogleApiClient();
    }

    // Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API
    protected synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
            //mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Activity Resumed");
        super.onResume();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        /*if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "mGoogleApiClient connect called");
        }*/
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Activity Paused");

        super.onPause();
        // Stop location updates to save battery,
        // but don't disconnect the GoogleApiClient object.
        //stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Activity Stopped");
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    // Runs when a GoogleApiClient object successfully connects
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "inside onConnected");
        // Check whether GPS enabled or not, If not then alert user to enable
        final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showSettingsAlert();
        } else {
            gpsEnabled = true;
        }

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (mLastLocation != null) {
            errStatusTv.setVisibility(View.INVISIBLE);
        } else {    // mLastLocation = null may be because
            //Toast.makeText(this, "No GPS Signal detected! Make sure location is enabled on the device.", Toast.LENGTH_LONG).show();
            //errStatusTv.setVisibility(View.VISIBLE);
            //errStatusTv.setText("No GPS Signal detected! Make sure location is enabled on the device");
        }
        if (gpsEnabled) {
            Log.d(TAG, "Location Updates Started");
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        // Toast.makeText(this, "Your App unable to access location. Please enable from App permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "onConnection suspended");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);


        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Trigger new location updates at interval
    // https://github.com/googlesamples/android-play-location/blob/master/LocationUpdates/app/src/main/java/com/google/android/gms/location/sample/locationupdates/MainActivity.java
    protected void startLocationUpdates() {
        Log.d(TAG, "inside startLocationUpdates");
        // Request location updates
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    // Removes location updates from the FusedLocationApi
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        // Get Address from Lat / Lng
        double mLat = mLastLocation.getLatitude();
        double mLng = mLastLocation.getLongitude();
        if (mLat != 0.0 && mLng != 0.0) {
            Double[] loc = {mLat, mLng};
            new GetAddressFromLatLngTask().execute(loc);
        }

        loc_lat = mLat; loc_lng = mLng;
    }



    @Override
    public void onClick(View v) {
        Attendance atd;

        switch (v.getId()) {
            case R.id.myaa_scanQR_Btn:
                Intent myIntent = new Intent(getApplicationContext(), MyAdi_MyAtt_QRScanActivity.class);
                //startActivity(myIntent);
                startActivityForResult(myIntent, MyQRScanAtivity_REQUEST);
                break;
            case R.id.myaa_in_Btn:
                //stopLocationUpdates();
                // Update attendance to the Database
                atd = new Attendance(empId, punchTimeStamp_SQLFmt, loc_lat, loc_lng, storeLocation, Attendance.PUNCHIN);
                new SaveAttendanceInDatabaseTask().execute(atd);
                //Toast.makeText(this, "Attendance [PUNCH-IN] Submitted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.myaa_out_Btn:
                //stopLocationUpdates();
                // Update attendance to the Database
                atd = new Attendance(empId, punchTimeStamp_SQLFmt, loc_lat, loc_lng, storeLocation, Attendance.PUNCHOUT);
                new SaveAttendanceInDatabaseTask().execute(atd);
                //Toast.makeText(this, "Attendance [Punch-OUT] Submitted.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // On QR Code Scan Button press
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Inside ", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyQRScanAtivity_REQUEST) {       // Check its the QRScanActivity request code
            if(resultCode == Activity.RESULT_OK) {          // Operation Succeeded
                String qrScanResult = data.getStringExtra("result");
                String storeDetails = extractStoreDetailsFromQR(qrScanResult);

                if (storeDetails.equalsIgnoreCase("ERROR")) {
                    errStatusTv.setVisibility(View.VISIBLE);
                    errStatusTv.setText("Scan not properly done or invalid QR Code. Please re-try.");
                    return;
                } else {
                    // set flag
                    qrCodeScanSuccess = true;

                    // Now that QR Code Scanned, Update Store Address & Hide Scan QR Button
                    scanQRBtn.setVisibility(View.GONE);
                    errStatusTv.setVisibility(View.INVISIBLE);

                    ((ImageView)findViewById(R.id.myaa_storeQR_Iv)).setVisibility(View.VISIBLE);
                    storeQRInfoTv.setVisibility(View.VISIBLE);
                    storeQRInfoTv.setText(storeDetails);

                    // Now that QR Code & Location are retrieved enable attendance punch-in/out buttons
                    inBtn.setVisibility(View.VISIBLE); outBtn.setVisibility(View.VISIBLE);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {    // Operation Cancelled
                Log.d(TAG, "MyAdi_MyAtt_QRScanActivity: Activity was cancelled");

                errStatusTv.setVisibility(View.VISIBLE);
                errStatusTv.setText("Do not cancel Scan activity. Please re-try.");
            } else {
                Log.d(TAG, "MyAdi_MyAtt_QRScanActivityActivity did not execute properly");

                errStatusTv.setVisibility(View.VISIBLE);
                errStatusTv.setText("Some issue scanning QR. Please re-try.");
            }
        }
    }

    String extractStoreDetailsFromQR(String scan) {
        String storeDetails = "";

        if (!scan.contains("ORG:Adishwar India Limited")) { // Not an Adishwar QR Code
            Log.d(TAG, "Not an Adishwar QR Code");
            return "ERROR";
        }

        if (scan.contains("BEGIN:VCARD")) {
            String[] scanArray = scan.split("\n");
            Log.d(TAG, "QRCode Lines = " + scanArray.length);

            for (int i = 0; i < scanArray.length; i++) {
                String scanItem = scanArray[i];
                Log.d("ScanArray", "Item [" + i + "]= " + scanItem);

                // Store Name
                if (scanItem.startsWith("N:")) {
                    scanItem = scanItem.replace("N:", "");
                    storeDetails += scanItem;

                    // get store location from name
                    storeLocation = scanItem.substring(scanItem.indexOf(" - ")+3, scanItem.length());
                }

                // Address
                if (scanItem.startsWith("ADR:")) {
                    scanItem = scanItem.replace("ADR:", "");
                    scanItem = scanItem.replace("\\,", ",");
                    storeDetails += ",\n" + scanItem;
                }
            }
        } else {        // QR Code doesnt contain VCARD
            //Error
            Log.d(TAG, "QR Code doesnt contain VCARD");
            return "ERROR";
        }


        return storeDetails;
    }

    // Function to show settings alert dialog
    // On pressing Settings button will launch GPS Settings Options
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Please click \'Settings\' to enable it?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                locTv.setText("Unable to detect GPS location!");
                locTv.setTextColor(Color.RED);
                errStatusTv.setText("GPS location not enabled!\nGo to \"Settings > Location \" (enable)");
                errStatusTv.setVisibility(View.VISIBLE);
                finish();
            }
        });

        errStatusTv.setText("");
        errStatusTv.setVisibility(View.INVISIBLE);

        // Showing Alert Message
        alertDialog.show();
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        // Set Font
        TextView attendTv = (TextView)findViewById(R.id.myaa_attend_Tv);
        attendTv.setTypeface(typeface, Typeface.BOLD);
        attendTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView atdpromptTv = (TextView)findViewById(R.id.myaa_atdprompt_Tv);
        empnameTv = (TextView)findViewById(R.id.myaa_empname_Tv);
        datetimeTv = (TextView)findViewById(R.id.myaa_datetime_Tv);

        locTv = (TextView)findViewById(R.id.myaa_location_Tv);
        storeQRInfoTv = (TextView)findViewById(R.id.myaa_storeQR_Tv);
        atdpromptTv.setTypeface(typeface2, Typeface.BOLD);
        empnameTv.setTypeface(typeface2); datetimeTv.setTypeface(typeface2);

        locTv.setTypeface(typeface2, Typeface.ITALIC); storeQRInfoTv.setTypeface(typeface2);

        errStatusTv = (TextView)findViewById(R.id.myaa_errStatus_Tv);
        errStatusTv.setTypeface(typeface2);

        scanQRBtn = (Button)findViewById(R.id.myaa_scanQR_Btn);
        inBtn = (Button)findViewById(R.id.myaa_in_Btn);
        outBtn = (Button)findViewById(R.id.myaa_out_Btn);
        scanQRBtn.setTypeface(typeface2); inBtn.setTypeface(typeface2); outBtn.setTypeface(typeface2);

    }

    private class GetAddressFromLatLngTask extends AsyncTask<Double, Integer, String> {
        ProgressBar getLocation_PB;
        double lat, lng = 0.0;
        private static final String REVERSE_GEOCODING_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
        private static final String GEOCODINGKEY = "&type=establishment&radius=50&key=AIzaSyBJd5v-jAY4f_HauLEXjrg1iA5FJ-iGi3o";

        @Override
        protected void onPreExecute() {
            getLocation_PB = (ProgressBar) findViewById(R.id.myaa_getLocProgress_Pb);
            getLocation_PB.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Double... params) {
            String mResult = "";

            if (params.length == 2) {
                lat = params[0].doubleValue();
                lng = params[1].doubleValue();
            }

            String mURL = REVERSE_GEOCODING_URL + lat + "," + lng + GEOCODINGKEY;

            try {
                URL url = new URL(mURL);
                HttpURLConnection httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setReadTimeout(10000);
                httpsURLConnection.setConnectTimeout(15000);
                httpsURLConnection.setDoInput(true);
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.connect();
                int mStatus = httpsURLConnection.getResponseCode();
                if (mStatus == 200)
                    mResult = readResponse(httpsURLConnection.getInputStream()).toString();
                Log.d("Result", mResult);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return mResult;
        }

        private String readResponse(InputStream inputStream) throws IOException, NullPointerException {
            String formattedAdd = "";

            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line);
            }
            String response = stringBuilder.toString();
            Log.d("JSONResponse", response);

            try {
                JSONObject responseJson = new JSONObject(response);

                if (responseJson.getJSONArray("results") != null ) {
                    JSONArray resultsList = responseJson.getJSONArray("results");
                    if (resultsList.getJSONObject(0) != null && resultsList.getJSONObject(0).getString("name") != null) {
                        formattedAdd += resultsList.getJSONObject(0).getString("name");
                    } else {
                        Log.d("JSON Parse Status", "Parsing Failed at CheckPoint#2");
                        return "ERROR";
                    }
                    if (resultsList.getJSONObject(0) != null && resultsList.getJSONObject(0).getString("vicinity") != null) {
                        formattedAdd += ",\n" + resultsList.getJSONObject(0).getString("vicinity");
                    } else {
                        Log.d("JSON Parse Status", "Parsing Failed at CheckPoint#3");
                        return "ERROR";
                    }

                    Log.d("Formatted Address", formattedAdd);
                } else {
                    Log.d("JSON Parse Status", "Parsing Failed at CheckPoint#1");
                    return "ERROR";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "Unable to detect location, trying ..." + response;
            }

            return formattedAdd;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            getLocation_PB.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            getLocation_PB.setVisibility(View.GONE);
            super.onPostExecute(s);
            if (s!= null && !s.contains("ZERO_RESULTS")) {  // location string is not null & does not contain ZERO_RESULTS
                locTv.setText(String.format("%s", s) + String.format("\n(%s: %f, %s: %f)", "LAT",
                        lat, "LNG", lng));
                // Change text color from default (blue) to dark purple
                locTv.setTextColor(Color.parseColor("#724889"));
                Typeface typeface = Typeface.createFromAsset(getAssets(),
                        String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
                locTv.setTypeface(typeface, Typeface.NORMAL);



                // Stop location updates
                stopLocationUpdates();

                // Show QR Scan Button, if the QR Code hasnt been already scanned
                if (!qrCodeScanSuccess) {
                    scanQRBtn.setVisibility(View.VISIBLE);
                    errStatusTv.setText("");
                    errStatusTv.setVisibility(View.INVISIBLE);
                }
            } else if (s!=null &&  s.contains("ZERO_RESULTS")) {
                errStatusTv.setText("Trying to detect location, please wait ...");
                errStatusTv.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SaveAttendanceInDatabaseTask extends AsyncTask<Attendance, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Attendance... params) {
            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/SubmitAttendance.php");


                //
                // Send POST Request
                //
                JSONObject attendanceJSON = new JSONObject();
                attendanceJSON.put("attendance", Attendance.toMap(params[0]));
                Log.d(getClass().getName(), "JSON to be Sent === " + attendanceJSON.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(attendanceJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(attendanceJSON.toString().getBytes());

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
                        return "ERROR | Error in Attendance Submission! | <Blank Response>";
                    }

                    Log.d(getClass().getName(), "Response JSON == " + sb.toString());
                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());
                        String attStatusResponseStr = responseJson.getString("AttendanceSubmitStatus");
                        boolean attendanceInsertError = ((responseJson.getString("error_status").equalsIgnoreCase("true")) ? true : false);

                        if (attendanceInsertError && attStatusResponseStr != null && attStatusResponseStr.equals("Attendance Already PUNCHED-IN"))  {
                            Log.d(TAG, "ERROR | You have already Punched-IN earlier today");
                            return "ERROR | You have already Punched-IN earlier today";
                        } else if (attendanceInsertError && attStatusResponseStr != null && attStatusResponseStr.equals("Attendance Already PUNCHED-OUT"))  {
                            Log.d(TAG, "ERROR | You have already Punched-OUT earlier today");
                            return "ERROR | You have already Punched-OUT earlier today";
                        } else if (attendanceInsertError && attStatusResponseStr != null && attStatusResponseStr.length()>0) // Status (error == true) received
                        {
                            Log.d(TAG, "ERROR | " + responseJson.getString("AttendanceSubmitStatus"));
                            return "ERROR | Log Status = " + responseJson.getString("AttendanceSubmitStatus");
                        } else if (!attendanceInsertError && attStatusResponseStr != null && attStatusResponseStr.equals("Attendance Submitted Successfully")) {
                            String attType = responseJson.getString("AttendanceType");
                            if (attType != null && attType.equalsIgnoreCase("IN")) {
                                Log.d(TAG, "SUCCESS | Attendance PUNCHED-IN");
                                return "SUCCESS | PUNCH-IN";
                            } else if (attType != null && attType.equalsIgnoreCase("OUT")) {
                                Log.d(TAG, "SUCCESS | Attendance PUNCHED-OUT");
                                return "SUCCESS | PUNCH-OUT";
                            } else {
                                return "SUCCESS | NONE";
                            }
                        } else {
                            Log.d(TAG, "ERROR | error_status missing");
                            return "ERROR | error_status missing!";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | HTTP Error in Attendance Submission! (" + responseCode + ")");
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

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(getClass().getName(), "Response == " + s);


            if (s.contains("SUCCESS")) {
                if (s.contains("PUNCH-IN")) {
                    SpannableString spannableString = new SpannableString("Attendance PUNCH-IN SUCCESSFUL!");
                    spannableString.setSpan(
                            new ForegroundColorSpan(getResources().getColor(android.R.color.holo_green_light)),
                            0,
                            spannableString.length(),
                            0);
                    Toast.makeText(MyAdi_MyAttendanceActivity.this, spannableString, Toast.LENGTH_LONG).show();

                    //Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Attendance STATUS] PUNCH-IN SUCCESSFUL!", Toast.LENGTH_LONG).show();
                } else if (s.contains("PUNCH-OUT")) {
                    SpannableString spannableString = new SpannableString("Attendance PUNCH-OUT SUCCESSFUL!");
                    spannableString.setSpan(
                            new ForegroundColorSpan(getResources().getColor(android.R.color.holo_green_light)),
                            0,
                            spannableString.length(),
                            0);
                    Toast.makeText(MyAdi_MyAttendanceActivity.this, spannableString, Toast.LENGTH_LONG).show();

                    //Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Attendance STATUS] PUNCH-OUT SUCCESSFUL!", Toast.LENGTH_LONG).show();
                }
            } else if (s.contains("ERROR | You have already Punched-IN earlier today")) {
                SpannableString spannableString = new SpannableString("Attendance PUNCH-IN Failed! \nAlready Punch-IN done for today.");
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                        0,
                        spannableString.length(),
                        0);
                Toast.makeText(MyAdi_MyAttendanceActivity.this, spannableString, Toast.LENGTH_LONG).show();


                //Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Attendance FAILURE] You have already Punched-IN earlier today from this store!", Toast.LENGTH_LONG).show();
            } else if (s.contains("ERROR | You have already Punched-OUT earlier today")) {
                SpannableString spannableString = new SpannableString("Attendance PUNCH-OUT Failed! \nAlready Punch-IN done for today.");
                spannableString.setSpan(
                        new ForegroundColorSpan(getResources().getColor(android.R.color.holo_red_light)),
                        0,
                        spannableString.length(),
                        0);
                Toast.makeText(MyAdi_MyAttendanceActivity.this, spannableString, Toast.LENGTH_LONG).show();

                //Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Attendance FAILURE] You have already Punched-OUT earlier today from this store!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyAdi_MyAttendanceActivity.this, "[Attendance ERROR] Attendance not submitted! Please retry", Toast.LENGTH_LONG).show();
            }

            // Go back to home page / activity
            Intent myIntent = new Intent(getApplicationContext(), MyAdishwarActivity.class);
            startActivity(myIntent);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo nwInfo = cm.getActiveNetworkInfo();

            if (nwInfo != null && nwInfo.isConnected()) {
                //Log.d(getClass().getName(), "Your mobile device is on "+ nwInfo.getTypeName()+" Network, State = "+nwInfo.getState().toString());
                return true;
            }
            else{
                //Log.d(getClass().getName(), "NetworkInfo is Null ||OR|| NetworkInfo isConnected() = False");
                return false;
            }
        } else {
            //Log.d(getClass().getName(), "Connectivity Manager Null");
            return false;
        }
    }


    // Checks whether the user has already granted permission to access location to the app.
    // In case NOT then prompt the user to grant the permission
    void checkLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else { // location access already granted
            locPermissionGranted = true;
        }
    }
}
