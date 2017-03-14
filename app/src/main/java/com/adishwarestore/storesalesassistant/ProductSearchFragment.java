package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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

/**
 * Created by dhiraj on 31-Dec-16.
 */

public class ProductSearchFragment extends Fragment implements View.OnClickListener {

    OnProductSearchButtonClickedInterface mCallback;
    Button prodSearchBtn = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_search, container, false);


        // Create Font
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        // Set TextView Font
        TextView product_search_Tv = (TextView)v.findViewById(R.id.product_search_tv);
        TextView prodcat_prompt_Tv = (TextView)v.findViewById(R.id.prodcatprompt_Tv);
        TextView prodcod_prompt_Tv = (TextView)v.findViewById(R.id.prodcod_Tv);
        TextView prodList_prompt_Tv = (TextView)v.findViewById(R.id.prodList_Tv);
        EditText prodCode_Et = (EditText)v.findViewById(R.id.prodCode_Et);
        Spinner prodList_Sp = (Spinner)v.findViewById(R.id.prodList_spinner);

        product_search_Tv.setTypeface(typeface); prodcat_prompt_Tv.setTypeface(typeface);
        prodcod_prompt_Tv.setTypeface(typeface); prodList_prompt_Tv.setTypeface(typeface); prodCode_Et.setTypeface(typeface);


        // Add a listener for the Radio & Button
        RadioButton naRadio = (RadioButton)v.findViewById(R.id.NA_RadioBtn);
        RadioButton ceRadio = (RadioButton)v.findViewById(R.id.CE_RadioBtn);
        RadioButton caRadio = (RadioButton)v.findViewById(R.id.CA_RadioBtn);
        RadioButton dapRadio = (RadioButton)v.findViewById(R.id.DAP_RadioBtn);
        prodSearchBtn = (Button)v.findViewById(R.id.prodSearchSubmit_Btn);

        naRadio.setOnClickListener(this);
        ceRadio.setOnClickListener(this);
        caRadio.setOnClickListener(this);
        dapRadio.setOnClickListener(this);
        prodSearchBtn.setOnClickListener(this);

        // Set Radio, Button Font
        naRadio.setTypeface(typeface); ceRadio.setTypeface(typeface); caRadio.setTypeface(typeface);
        dapRadio.setTypeface(typeface); prodSearchBtn.setTypeface(typeface);

        // Set default category & type
        naRadio.setChecked(true);
        // Create an ArrayAdapter using the string array and a default spinner layout
        MySpinnerAdapter adapter = new MySpinnerAdapter(
                v.getContext(),
                R.layout.view_spinner_item,
                Arrays.asList(getResources().getStringArray(R.array.NA_prodList))
        );
        prodList_Sp.setAdapter(adapter);

        // On Done/Next keypress on prodcode entry soft-keyboard press the SEARCH submit button
        prodCode_Et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    textView.clearFocus();
                    prodSearchBtn.requestFocus();
                    prodSearchBtn.performClick();
                }
                return true;
            }
        });

        // Hide progress bar
        ProgressBar pb = (ProgressBar)v.findViewById(R.id.progress_Pb);
        pb.setVisibility(View.GONE);

        return v;
    }


    public interface OnProductSearchButtonClickedInterface {
        public void onProductSearchButtonClicked(String prodcat, String prod, String prodcod);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnProductSearchButtonClickedInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductSearchButtonClickedInterface");
        }
    }

    @Override
    public void onClick(View v) {
        Spinner prodListSpinner = (Spinner)getActivity().findViewById(R.id.prodList_spinner);
        MySpinnerAdapter adapter;

        switch (v.getId()) {
            case R.id.NA_RadioBtn:
                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.NA_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.CE_RadioBtn:
                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.CE_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.CA_RadioBtn:
                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.CA_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.DAP_RadioBtn:
                // Create an ArrayAdapter using the string array and a default spinner layout
                adapter = new MySpinnerAdapter(
                        v.getContext(),
                        R.layout.view_spinner_item,
                        Arrays.asList(getResources().getStringArray(R.array.DAP_prodList))
                );
                prodListSpinner.setAdapter(adapter);
                break;
            case R.id.prodSearchSubmit_Btn:
                // Hidekeyboard
                hideKeyboard();

                // A Category must be selected
                RadioGroup prodcatRG = (RadioGroup)getActivity().findViewById(R.id.ProdCat_RadioGrp);
                int prodcatSelected = prodcatRG.getCheckedRadioButtonId();
                if (prodcatSelected == -1) {
                    Toast.makeText(getActivity(), "[ERROR] Select a Product Category!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the inputs provided
                // //Product Category
                RadioButton rb = (RadioButton)getActivity().findViewById(prodcatSelected);
                String prodcat = rb.getText().toString();
                if (prodcat.equalsIgnoreCase("all")) { // If Category = ALL
                    prodcat = "none";
                }
                // //Product
                Spinner spnr = (Spinner)getActivity().findViewById(R.id.prodList_spinner);
                String prod = spnr.getSelectedItem().toString();
                if (prod.equalsIgnoreCase("Select One")) {
                    Toast.makeText(getActivity(), "[ERROR] Please Select a Product!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (prod.trim().equalsIgnoreCase("all")) {
                    prod = "none";
                }

                // //Product Code
                EditText et = (EditText)getActivity().findViewById(R.id.prodCode_Et);
                String prodcode = et.getText().toString();
                if (prodcode.trim().equals("")) {
                    Toast.makeText(getActivity(), "[ERROR] Please Enter a Product Code!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Toast.makeText(getActivity(), "[SUCCESS] Category = " +  prodcat + ", Product = " + prod + ", Code = " + prodcode, Toast.LENGTH_SHORT).show();
                ((ProgressBar)getActivity().findViewById(R.id.progress_Pb)).setVisibility(View.VISIBLE);
                mCallback.onProductSearchButtonClicked(prodcat, prod, prodcode);
                break;
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
