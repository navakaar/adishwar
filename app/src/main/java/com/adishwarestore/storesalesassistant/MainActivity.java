package com.adishwarestore.storesalesassistant;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.R.attr.accessibilityEventTypes;
import static android.R.attr.type;
import static android.R.attr.typeface;

public class MainActivity extends Activity implements View.OnClickListener {

    // Request code to check location access allowed or not
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setScreenFonts();


        // Register click listener
        ImageView productDetailsIv = (ImageView)findViewById(R.id.am_product_details_Iv);
        ImageView myadishwarIv = (ImageView)findViewById(R.id.am_myadishwar_Iv);
        ImageView visitorLogIv = (ImageView)findViewById(R.id.am_visit_log_Iv);
        ImageView salesLogIv = (ImageView)findViewById(R.id.am_sales_log_Iv);
        ImageView stockCheckIv = (ImageView)findViewById(R.id.am_stock_check_Iv);
        ImageView newsOffersIv = (ImageView)findViewById(R.id.am_news_offers_Iv);

        productDetailsIv.setOnClickListener(this);  myadishwarIv.setOnClickListener(this);
        visitorLogIv.setOnClickListener(this);      salesLogIv.setOnClickListener(this);
        stockCheckIv.setOnClickListener(this);      newsOffersIv.setOnClickListener(this);

        if (!isNetworkAvailable()) {         // Check that the phone is connected to the data network
            Toast.makeText(MainActivity.this, "[Error] Your Phone data connection is NOT ON; Please enable data (2g/3g/4g/wifi)", Toast.LENGTH_LONG).show();
            return;
        }
        // Check/Ask for location access for the App (required for Attendance feature)
        checkLocationPermissionGranted(); // required in API 23 & greater
    }

    void setScreenFonts() {
        // Create Fonts
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "bauhaus.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set TextView Font
        TextView product_details_Tv = (TextView)findViewById(R.id.am_product_details_Tv);
        TextView news_offers_Tv = (TextView)findViewById(R.id.am_news_offers_Tv);
        TextView visit_log_Tv = (TextView)findViewById(R.id.am_visit_log_Tv);
        TextView sales_log_Tv = (TextView)findViewById(R.id.am_sales_log_Tv);
        TextView stock_check_Tv = (TextView)findViewById(R.id.am_stock_check_Tv);
        TextView emp_adda_Tv = (TextView)findViewById(R.id.am_emp_adda_Tv);

        product_details_Tv.setTypeface(typeface2, Typeface.BOLD); news_offers_Tv.setTypeface(typeface2, Typeface.BOLD);
        visit_log_Tv.setTypeface(typeface2, Typeface.BOLD); sales_log_Tv.setTypeface(typeface2, Typeface.BOLD);
        stock_check_Tv.setTypeface(typeface2, Typeface.BOLD); emp_adda_Tv.setTypeface(typeface2, Typeface.BOLD);

        return;
    }

    @Override
    public void onClick(View v) {
        Intent myIntent;

        switch (v.getId()) {
            case R.id.am_product_details_Iv:
                myIntent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                startActivity(myIntent);
                break;
            case R.id.am_myadishwar_Iv:
                myIntent = new Intent(getApplicationContext(), MyAdishwarActivity.class);
                startActivity(myIntent);
                break;
            case R.id.am_visit_log_Iv:
                myIntent = new Intent(getApplicationContext(), VisitorLogActivity.class);
                startActivity(myIntent);
                break;
            case R.id.am_sales_log_Iv:
                myIntent = new Intent(getApplicationContext(), SalesLogActivity.class);
                startActivity(myIntent);
                break;
            case R.id.am_stock_check_Iv:
                myIntent = new Intent(getApplicationContext(), StockCheckActivity.class);
                startActivity(myIntent);
                break;
            case R.id.am_news_offers_Iv:
                myIntent = new Intent(getApplicationContext(), LatestNewsActivity.class);
                startActivity(myIntent);
                break;
        }
        return;
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "App has been denied device location access. Some App features will not function properly!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
        return;
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
}
