package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

/**
 * Created by dhirb on 25-01-2017.
 */

public class StockCheckActivity extends Activity implements View.OnClickListener {
    Button submitBtn = null;
    TextView stockStatus_Tv = null, proddetails1_Tv = null, proddetails2_Tv = null, proddetails3_Tv = null, proddetails_Tv = null;
    EditText prodcode_Et = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockcheck);

        setScreenFonts();

        submitBtn.setOnClickListener(this);
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView stockcheck_Tv = (TextView) findViewById(R.id.asc_stkchk_Tv);
        stockcheck_Tv.setTypeface(typeface, Typeface.BOLD);
        stockcheck_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView searchdetails_Tv = (TextView) findViewById(R.id.asc_searchheader_Tv);
        proddetails_Tv = (TextView) findViewById(R.id.asc_proddetailsheader_Tv);
        TextView prodcode_Tv = (TextView) findViewById(R.id.asc_pcode_Tv);
        prodcode_Et = (EditText) findViewById(R.id.asc_pcode_Et);
        searchdetails_Tv.setTypeface(typeface2, Typeface.BOLD); searchdetails_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        proddetails_Tv.setTypeface(typeface2, Typeface.BOLD_ITALIC); proddetails_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        prodcode_Tv.setTypeface(typeface2); prodcode_Et.setTypeface(typeface2);

        submitBtn = (Button) findViewById(R.id.asc_submit_Btn);
        submitBtn.setTypeface(typeface2);

        stockStatus_Tv = (TextView) findViewById(R.id.asc_stockstatus_Tv);
        //stockStatus_Tv.setTypeface(typeface2, Typeface.BOLD); stockStatus_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        stockStatus_Tv.setTypeface(typeface2);

        proddetails1_Tv = (TextView) findViewById(R.id.asc_proddetails1_Tv);
        proddetails2_Tv = (TextView) findViewById(R.id.asc_proddetails2_Tv);
        proddetails3_Tv = (TextView) findViewById(R.id.asc_proddetails3_Tv);
        proddetails1_Tv.setTypeface(typeface2); proddetails2_Tv.setTypeface(typeface2); proddetails3_Tv.setTypeface(typeface2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.asc_submit_Btn:
                hideKeyboard();

                String pcode = prodcode_Et.getText().toString().trim();
                if (pcode.length() <= 3 || pcode.contains(" ")) {
                    Toast.makeText(this, "Please enter a valid Product Code", Toast.LENGTH_LONG).show();
                    return;
                }

                proddetails_Tv.setVisibility(View.VISIBLE);
                proddetails1_Tv.setVisibility(View.VISIBLE);
                proddetails2_Tv.setText("Product Code : SAMSUNG - " + pcode);
                proddetails2_Tv.setVisibility(View.VISIBLE);

                Random rand = new Random();
                int x = rand.nextInt(30)+2; int y = rand.nextInt(10);
                //30 is the maximum and the 1 is our minimum
                String invStr = proddetails3_Tv.getText().toString();
                invStr = invStr.replace("12", x+""); invStr = invStr.replace("5", y+"");
                proddetails3_Tv.setText(invStr);
                proddetails3_Tv.setVisibility(View.VISIBLE);
                stockStatus_Tv.setVisibility(View.VISIBLE);
                stockStatus_Tv.setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT);
                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}