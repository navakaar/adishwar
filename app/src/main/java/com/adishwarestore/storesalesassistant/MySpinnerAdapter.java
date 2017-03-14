package com.adishwarestore.storesalesassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dhiraj on 04-Jan-17.
 */

public class MySpinnerAdapter extends ArrayAdapter {

    Typeface typeface;

    public MySpinnerAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        // Initialise custom font
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/centurygothic.ttf");
    }

    // Affects default (closed) state of the spinner
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(typeface);
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(typeface);
        return view;
    }
}
