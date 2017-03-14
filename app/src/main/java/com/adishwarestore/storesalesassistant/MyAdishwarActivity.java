package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by dhiraj on 18-Jan-17.
 */

public class MyAdishwarActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadishwar);

        // Register click listener
        ImageView myAttendanceIv = (ImageView)findViewById(R.id.amy_qr_myattendance_Iv);
        ImageView myPerformanceIv = (ImageView)findViewById(R.id.amy_myperformance_Iv);
        ImageView empRecogIv = (ImageView)findViewById(R.id.amy_emprecog_Iv);
        ImageView empGreetIv = (ImageView)findViewById(R.id.amy_empgreet_Iv);
        myAttendanceIv.setOnClickListener(this); myPerformanceIv.setOnClickListener(this);
        empRecogIv.setOnClickListener(this); empGreetIv.setOnClickListener(this);

        setScreenFonts();
    }

    @Override
    public void onClick(View v) {
        Intent myIntent;

        switch (v.getId()) {
            case R.id.amy_qr_myattendance_Iv:
                myIntent = new Intent(getApplicationContext(), MyAdi_MyAttendanceActivity.class);
                startActivity(myIntent);
                break;
            case R.id.amy_myperformance_Iv:
                myIntent = new Intent(getApplicationContext(), MyAdi_MyPerformanceActivity.class);
                startActivity(myIntent);
                break;
            case R.id.amy_emprecog_Iv:
                myIntent = new Intent(getApplicationContext(), MyAdi_EmpRecognition.class);
                startActivity(myIntent);
                break;
            case R.id.amy_empgreet_Iv:
                myIntent = new Intent(getApplicationContext(), MyAdi_GreetingsActivity.class);
                startActivity(myIntent);
                break;
        }
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView myadishwar_Tv = (TextView) findViewById(R.id.amy_myadishwar_Tv);
        myadishwar_Tv.setTypeface(typeface, Typeface.BOLD); myadishwar_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView myattendance_Tv = (TextView) findViewById(R.id.amy_myattendance_Tv);
        TextView myperformance_Tv = (TextView) findViewById(R.id.amy_myperformance_Tv);
        myattendance_Tv.setTypeface(typeface2); myperformance_Tv.setTypeface(typeface2);

        TextView emprecog_Tv = (TextView) findViewById(R.id.amy_emprecog_Tv);
        TextView storerecog_Tv = (TextView) findViewById(R.id.amy_storerecog_Tv);
        emprecog_Tv.setTypeface(typeface2); storerecog_Tv.setTypeface(typeface2);

        //TextView bestsellers_Tv = (TextView) findViewById(R.id.amy_bestsellers_Tv);
        TextView empGreet_Tv = (TextView) findViewById(R.id.amy_empgreet_Tv);
        //bestsellers_Tv.setTypeface(typeface2);
        empGreet_Tv.setTypeface(typeface2);

        return;
    }
}
