package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dhirb on 25-01-2017.
 */

public class MyAdi_MyPerformanceActivity extends Activity {
    private Typeface typeface2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myadi_myperformance);

        setScreenFonts();

        BarChart vislogBarchart = (BarChart)findViewById(R.id.myamp_vperfstat_Barchart);
        BarChart slslogBarchart = (BarChart)findViewById(R.id.myamp_sperfstat_Barchart);


        BarData visitordata = new BarData(getXaxisValue(), getVisitorDataSet());
        vislogBarchart.setData(visitordata);
        vislogBarchart.setDescription(" ");
        vislogBarchart.animateXY(2000, 2000);
        vislogBarchart.invalidate();

        BarData salesdata = new BarData(getXaxisValue(), getSalesDataSet());
        slslogBarchart.setData(salesdata);
        slslogBarchart.setDescription(" ");
        slslogBarchart.animateXY(2000, 2000);
        slslogBarchart.invalidate();

        // set font
        visitordata.setValueTypeface(typeface2);
        salesdata.setValueTypeface(typeface2);
        vislogBarchart.setDescriptionTypeface(typeface2);
        slslogBarchart.setDescriptionTypeface(typeface2);
    }

    void setScreenFonts() {
        // Create Font
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));


        // Set Font
        TextView myperf_Tv = (TextView) findViewById(R.id.myamp_perf_Tv);
        myperf_Tv.setTypeface(typeface, Typeface.BOLD);
        myperf_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        TextView vperfstat_Tv = (TextView) findViewById(R.id.myamp_vperfstat_Tv);
        TextView sperfstat_Tv = (TextView) findViewById(R.id.myamp_sperfstat_Tv);
        vperfstat_Tv.setTypeface(typeface2, Typeface.BOLD); vperfstat_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        sperfstat_Tv.setTypeface(typeface2, Typeface.BOLD); sperfstat_Tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
    }

    private ArrayList<BarDataSet> getVisitorDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(78, 0); // Aug
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(90, 1); // Sep
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(80, 2); // Oct
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(45, 3); // Nov
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(60, 4); // Dec
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(58, 5); // Jan
        valueSet1.add(v1e6);


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Monthly Visitor Log");
        barDataSet1.setColor(Color.rgb(255, 69, 0));
        //barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        // set font
        barDataSet1.setValueTypeface(typeface2);
        barDataSet1.setValueTextSize(12f);

        return dataSets;
    }

    private ArrayList<BarDataSet> getSalesDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(202500, 0); // Aug
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(307000, 1); // Sep
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(450000, 2); // Oct
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(110000, 3); // Nov
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(221750, 4); // Dec
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(380500, 5); // Jan
        valueSet1.add(v1e6);


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Monthly Sales Log");
        barDataSet1.setColor(Color.rgb(102, 204, 0));
        //barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        // set font
        barDataSet1.setValueTypeface(typeface2);
        barDataSet1.setValueTextSize(10f);

        return dataSets;
    }

    private ArrayList<String> getXaxisValue() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("AUG");
        xAxis.add("SEP");
        xAxis.add("OCT");
        xAxis.add("NOV");
        xAxis.add("DEC");
        xAxis.add("JAN");

        return xAxis;
    }
}
