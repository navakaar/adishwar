package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhiraj on 16-Jan-17.
 */

public class VisitorLogVDetailsFragment extends Fragment implements View.OnClickListener {
    private EditText clientName_Et = null, clientPh_Et = null, clientLoc_Et = null;
    private Button next_Btn = null;

    private String clientName = "", clientPhone = "", clientLocation = "";
    private Visitor visitor = null;

    private OnClientDetailsNextButtonClickedInterface mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visitorlog_vdetails, container, false);

        setScreenFonts(v);
        next_Btn.setOnClickListener(this);

        // Hide keyboard on Next/Done keypress on PINCODE entry
        clientLoc_Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    textView.clearFocus();
                    next_Btn.requestFocus();
                    //next_Btn.performClick();
                }
                return true;
            }
        });

        return v;
    }

    void captureInputs() {
        visitor = new Visitor(clientName, clientPhone, clientLocation,
                                "", "", "", "", "", 0, 0);
        visitor.print();

        return;
    }

    boolean validateInputs() {
        // Visitor Name cannot be blank
        clientName = clientName_Et.getText().toString().trim();
        if (clientName.length() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Enter Client Name", Toast.LENGTH_LONG).show();
            return false;
        }

        // Phone Number cannot be blank or less than 10 digits
        clientPhone = clientPh_Et.getText().toString().trim();
        if (clientPhone.length() == 0 || clientPhone.length() < 10) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Enter a 10-digit Client Mobile Number", Toast.LENGTH_LONG).show();
            return false;
        }
        // Phone number cannot contain any special characters, only digits allowed
        Pattern pattern = Pattern.compile("[*+#()/N-]");
        Matcher matcher = pattern.matcher(clientPhone);
        if (matcher.find()) {
            // contains special characters
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Enter numerical digits only, No other characters allowed!", Toast.LENGTH_LONG).show();
            return false;
        }

        // Visitor Location cannot be blank or less than 10 digits
        clientLocation = clientLoc_Et.getText().toString().trim();
        if (clientLocation.length() == 0 || clientLocation.length() < 6) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Enter a 6-digit Client Location Pincode", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    void setScreenFonts(View v) {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView visDetails_Tv = (TextView) v.findViewById(R.id.fvlvd_visdetails_Tv);
        TextView stepCount_Tv = (TextView) v.findViewById(R.id.fvlvd_stepcount_Tv);
        visDetails_Tv.setTypeface(typeface, Typeface.BOLD);  visDetails_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        stepCount_Tv.setTypeface(typeface, Typeface.BOLD_ITALIC);  stepCount_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);


        TextView clientName_Tv = (TextView) v.findViewById(R.id.fvlvd_vname_Tv);
        clientName_Et = (EditText) v.findViewById(R.id.fvlvd_vname_Et);
        clientName_Tv.setTypeface(typeface); clientName_Et.setTypeface(typeface);

        TextView clientPh_Tv = (TextView) v.findViewById(R.id.fvlvd_vphone_Tv);
        clientPh_Et = (EditText) v.findViewById(R.id.fvlvd_vphone_Et);
        clientPh_Tv.setTypeface(typeface); clientPh_Et.setTypeface(typeface);

        TextView clientLoc_Tv = (TextView) v.findViewById(R.id.fvlvd_vloc_Tv);
        clientLoc_Et = (EditText) v.findViewById(R.id.fvlvd_vloc_Et);
        clientLoc_Tv.setTypeface(typeface); clientLoc_Et.setTypeface(typeface);


        next_Btn = (Button) v.findViewById(R.id.fvlvd_next_Btn);
        next_Btn.setTypeface(typeface);

        return;
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fvlvd_next_Btn:
                // On form submit validate inputs and proceed if true
                if (!validateInputs())
                    return;
                //else
                    //Toast.makeText(getActivity().getApplicationContext(), "[SUCCESS] Valid Visitor Inputs", Toast.LENGTH_LONG).show();
                // capture inputs
                captureInputs();

                // callback parent activity
                mCallback.OnClientDetailsNextButtonClicked(visitor);
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnClientDetailsNextButtonClickedInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientDetailsNextButtonClickedInterface");
        }
    }

    public interface OnClientDetailsNextButtonClickedInterface {
        public void OnClientDetailsNextButtonClicked(Visitor v);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
