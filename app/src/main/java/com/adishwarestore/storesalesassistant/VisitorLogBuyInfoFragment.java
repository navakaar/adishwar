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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by dhiraj on 17-Jan-17.
 */

public class VisitorLogBuyInfoFragment extends Fragment implements View.OnClickListener {
    private RadioGroup buyTimeFmRG = null;
    private RadioButton stRadio = null, mdRadio = null, lgpRadio = null;
    EditText budmin_Et = null, budmax_Et = null, comm_Et = null;

    private Button next_Btn = null;

    private int minBud = 0, maxBud = 0;
    private Visitor visitor = null;

    private OnBuyInfoNextButtonClickedInterface mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_visitorlog_buyinfo, container, false);

        visitor = (Visitor) getActivity().getIntent().getSerializableExtra("visitor");
        setScreenFonts(v);

        next_Btn.setOnClickListener(this);

        // On Next/Done keypress on MAX budget, send cursor to comments ET
        budmax_Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    textView.clearFocus();
                    comm_Et.requestFocus();
                    comm_Et.performClick();
                }
                return true;
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fvlbi_next_Btn:
                if (!validateInputs())
                    return;

                //Capture Inputs
                captureInputs();

                // Activity Callback
                mCallback.OnBuyInfoNextButtonClicked(visitor);
                break;
        }

    }

    void captureInputs() {
        RadioButton rb = (RadioButton)getActivity().findViewById(buyTimeFmRG.getCheckedRadioButtonId());
        visitor.setBuyTerm(rb.getText().toString());

        visitor.setMinBudget(minBud); visitor.setMaxBudget(maxBud);
        visitor.setComments(comm_Et.getText().toString().trim());

        visitor.print();
        return;
    }

    boolean validateInputs() {
        buyTimeFmRG = (RadioGroup)getActivity().findViewById(R.id.fvlbi_buyTimeFm_RadioGrp);
        if (buyTimeFmRG.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Select Client Purchase Timeframe!", Toast.LENGTH_LONG).show();
            return false;
        }


        // Max value should not be less than Min value
        String minBudStr = budmin_Et.getText().toString().trim();
        String maxBudStr = budmax_Et.getText().toString().trim();
        if ( minBudStr.length()>0 && maxBudStr.length()>0) {
            minBud = Integer.parseInt(minBudStr);
            maxBud = Integer.parseInt(maxBudStr);
            if (maxBud < minBud) {
                Toast.makeText(getActivity().getApplicationContext(), "[ERROR] Budget - MAX value cannot be less than MIN!", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    void setScreenFonts(View v) {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        TextView budtf_Tv = (TextView) v.findViewById(R.id.fvlbi_purchase_Tv);
        TextView stepCount_Tv = (TextView) v.findViewById(R.id.fvlbi_stepcount_Tv);
        budtf_Tv.setTypeface(typeface, Typeface.BOLD); budtf_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        stepCount_Tv.setTypeface(typeface, Typeface.BOLD_ITALIC);  stepCount_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        TextView buyTimeFm_Tv = (TextView) v.findViewById(R.id.fvlbi_buytf_Tv);
        buyTimeFm_Tv.setTypeface(typeface);

        stRadio = (RadioButton) v.findViewById(R.id.fvlbi_SHT_RadioBtn);
        mdRadio = (RadioButton) v.findViewById(R.id.fvlbi_MED_RadioBtn);
        lgpRadio = (RadioButton) v.findViewById(R.id.fvlbi_LNG_RadioBtn);
        stRadio.setTypeface(typeface); mdRadio.setTypeface(typeface); lgpRadio.setTypeface(typeface);

        TextView bud_Tv = (TextView) v.findViewById(R.id.fvlbi_budget_Tv);
        budmin_Et = (EditText) v.findViewById(R.id.fvlbi_budmin_Et);
        TextView buddash_Tv = (TextView) v.findViewById(R.id.fvlbi_budgetdash_Tv);
        budmax_Et = (EditText) v.findViewById(R.id.fvlbi_budmax_Et);
        bud_Tv.setTypeface(typeface);  budmin_Et.setTypeface(typeface); buddash_Tv.setTypeface(typeface); budmax_Et.setTypeface(typeface);

        TextView comm_Tv = (TextView) v.findViewById(R.id.fvlbi_comm_Tv);
        comm_Et = (EditText) v.findViewById(R.id.fvlbi_comm_Et);
        comm_Tv.setTypeface(typeface);  comm_Et.setTypeface(typeface);

        next_Btn = (Button) v.findViewById(R.id.fvlbi_next_Btn);
        next_Btn.setTypeface(typeface);
        
        return;
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
            mCallback = (OnBuyInfoNextButtonClickedInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnBuyInfoNextButtonClickedInterface");
        }
    }

    public interface OnBuyInfoNextButtonClickedInterface {
        public void OnBuyInfoNextButtonClicked(Visitor v);
    }
}
