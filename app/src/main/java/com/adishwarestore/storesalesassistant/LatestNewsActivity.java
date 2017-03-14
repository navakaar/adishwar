package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.ListActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by dhirb on 02-03-2017.
 */

public class LatestNewsActivity extends Activity {
    ListView newsList;
    String[] headlines =    {
                            "New Adishwar Store Launched in Hyderabad",
                            "Ugadi Festival Offer For Karnataka",
                            "Adishwar wins LG's Most Innovative Dealer Award",
                            "Christmas and New Year Sale",
                            "Adishwar Store Manager's Annual Meet"
                            };
    String[] newsBody =     {
                            "Adishwar launches a new store in the city of Hyderabad. This is Adishwar's 18th store, covering the north-east part of the city.",
                            "Adishwar launches the Ugadi festival sale and offers from 20th Nov to 5th Oct. This sale is for the Karnataka region only.",
                            "Adishwar India Ltd has been named LG's most innovative dealer of the year in the Consumer Appliances (CA) category. This award is given due to very eficient and transparent operations of Adishwar India stores. The award will be presented to Adishwar at a ceremony in Mumbai in March, 2017.",
                            "Adishwar launches the Christmas and New Year sale from 22nd Dec to 5th Jan. This sale is for all Adishwar stores across India.",
                            "Annual meeting of all store managers of Adishwar India will be held on 15th April, 2017 at Head Office, Rajaji Nagar, Bangalore"
                            };
    Integer[] imageIds =    {
                            R.drawable.newstore,
                            R.drawable.sale,
                            R.drawable.award,
                            R.drawable.sale,
                            R.drawable.datetime
                            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latestnews);



        Typeface typeface = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "monotype_corsiva.ttf"));
        Typeface typeface2 = Typeface.createFromAsset(getAssets(),
                String.format(Locale.US, "fonts/%s", "theboldfont.ttf"));

        TextView headlinesTv = (TextView)findViewById(R.id.aln_newsheadlines_Tv);
        //TextView latestNewsTv = (TextView)findViewById(R.id.aln_latestnews_Tv);

        //latestNewsTv.setTypeface(typeface);
        headlinesTv.setText("Adishwar Opens New Store in Hyderabad - Kukatpally");
        headlinesTv.setTypeface(typeface2);


        NewsListAdaptor newsListAdaptor = new NewsListAdaptor(LatestNewsActivity.this, headlines, newsBody, imageIds);
        ListView newsList = (ListView)findViewById(R.id.newslist_Lv);
        newsList.setAdapter(newsListAdaptor);

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(LatestNewsActivity.this, "News: " + headlines[position], Toast.LENGTH_LONG).show();
            }
        });
    }
}
