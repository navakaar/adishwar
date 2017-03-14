package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dhiraj on 16-Jan-17.
 */

public class VisitorLogPDetailsFragment extends Fragment implements View.OnClickListener {
    private EditText prodCod_Et = null;
    private Button next_Btn = null;
    private RadioGroup catRG = null;
    private RadioButton ceRadio = null, caRadio = null, dapRadio = null, othRadio = null;
    private Spinner prodListSpinner = null;

    private Visitor visitor = null;

    private OnProductDetailsNextButtonClickedInterface mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visitorlog_pdetails, container, false);

        visitor = (Visitor)getActivity().getIntent().getSerializableExtra("visitor");
        setScreenFonts(v);

        // Register click listeners
        next_Btn.setOnClickListener(this);
        ceRadio.setOnClickListener(this); caRadio.setOnClickListener(this);
        dapRadio.setOnClickListener(this); othRadio.setOnClickListener(this);

        // First time the activity is created, default category (CE) & product type
        if (savedInstanceState == null) {
            // Set default category & type
            ceRadio.setChecked(true);
            // Create an ArrayAdapter using the string array and a default spinner layout
            prodListSpinner = (Spinner) v.findViewById(R.id.fvlpd_prodList_spinner);
            MySpinnerAdapter adapter = new MySpinnerAdapter(
                    getActivity().getApplicationContext(),
                    R.layout.view_spinner_item,
                    Arrays.asList(getResources().getStringArray(R.array.CE_prodList))
            );
            prodListSpinner.setAdapter(adapter);
        }

        // Hide keyboard on Next/Done keypress on ProdCode entry
        prodCod_Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        RadioButton rb = (RadioButton)getActivity().findViewById(catRG.getCheckedRadioButtonId());
        String prodcat = rb.getText().toString();

        visitor.setProductCategory(prodcat);
        visitor.setProductType(prodListSpinner.getSelectedItem().toString());
        visitor.setProductCode(prodCod_Et.getText().toString().trim());

        visitor.print();

        return;
    }

    boolean validateInputs() {
        // A Category must be selected
        catRG = (RadioGroup)getActivity().findViewById(R.id.fvlpd_ProdCat_RadioGrp);
        int catRadioButtonId = catRG.getCheckedRadioButtonId();
        if (catRadioButtonId == -1) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Select a Product Category!", Toast.LENGTH_LONG).show();
            return false;
        }
        // Product type not selected
        if ( (catRadioButtonId == R.id.fvlpd_CA_RadioBtn ||
                catRadioButtonId == R.id.fvlpd_CE_RadioBtn ||
                catRadioButtonId == R.id.fvlpd_DAP_RadioBtn) && prodListSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Select a Product!", Toast.LENGTH_LONG).show();
            return false;
        }

        // Product Code is OPTIONAL so no validation needed

        return true;
    }
    
    void setScreenFonts(View v) {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
        
        TextView prodInt_Tv = (TextView) v.findViewById(R.id.fvlpd_proddetails_Tv);
        TextView stepCount_Tv = (TextView) v.findViewById(R.id.fvlpd_stepcount_Tv);
        prodInt_Tv.setTypeface(typeface, Typeface.BOLD); prodInt_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        stepCount_Tv.setTypeface(typeface, Typeface.BOLD_ITALIC);  stepCount_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        ceRadio = (RadioButton) v.findViewById(R.id.fvlpd_CE_RadioBtn);
        caRadio = (RadioButton) v.findViewById(R.id.fvlpd_CA_RadioBtn);
        dapRadio = (RadioButton) v.findViewById(R.id.fvlpd_DAP_RadioBtn);
        othRadio = (RadioButton) v.findViewById(R.id.fvlpd_OTH_RadioBtn);
        ceRadio.setTypeface(typeface); caRadio.setTypeface(typeface);
        dapRadio.setTypeface(typeface); othRadio.setTypeface(typeface);

        TextView prodList_Tv = (TextView) v.findViewById(R.id.fvlpd_prodList_Tv);
        prodList_Tv.setTypeface(typeface);

        TextView prodCod_Tv = (TextView) v.findViewById(R.id.fvlpd_prodcod_Tv);
        prodCod_Et = (EditText) v.findViewById(R.id.fvlpd_prodcod_Et);
        prodCod_Tv.setTypeface(typeface); prodCod_Et.setTypeface(typeface);

        next_Btn = (Button) v.findViewById(R.id.fvlpd_next_Btn);
        next_Btn.setTypeface(typeface);

        return;
    }
    
    @Override
    public void onClick(View v) {
        MySpinnerAdapter adapter;
        prodListSpinner = (Spinner) getActivity().findViewById(R.id.fvlpd_prodList_spinner);


        switch (v.getId()) {
            case R.id.fvlpd_CA_RadioBtn:
                // For each category selected, populate the related poduct types
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.CA_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.fvlpd_CE_RadioBtn:
                // For each category selected, populate the related poduct types
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.CE_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.fvlpd_DAP_RadioBtn:
                // For each category selected, populate the related poduct types
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.DAP_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.fvlpd_OTH_RadioBtn:
                // For each category selected, populate the related poduct types
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.OTH_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.fvlpd_next_Btn:
                // On form submit validate inputs and proceed if true
                if (!validateInputs())
                    return;
                //else
                    //Toast.makeText(getActivity().getApplicationContext(), "[SUCCESS] Valid Product Inputs", Toast.LENGTH_LONG).show();
                // Capture inputs provided
                captureInputs();

                // callback parent activity
                mCallback.OnProductDetailsNextButtonClicked(visitor);

                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnProductDetailsNextButtonClickedInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductDetailsNextButtonClickedInterface");
        }
    }

    public interface OnProductDetailsNextButtonClickedInterface {
        public void OnProductDetailsNextButtonClicked(Visitor v);
    }
}
