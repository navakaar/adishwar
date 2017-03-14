package com.adishwarestore.storesalesassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by dhirb on 02-03-2017.
 */

public class NewsListAdaptor extends ArrayAdapter<String> {
    private final Context context;
    private final String[] headline;
    private final String[] body;
    private final Integer[] imageId;

    public NewsListAdaptor(Context context, String[] headline, String[] body, Integer[] imageId) {
        super(context, R.layout.news_list_row, headline);
        this.context = context;
        this.headline = headline;
        this.body = body;
        this.imageId = imageId;
    }

    // The ListView instance calls the getView() method on the adapter for each data element.
    // In this method the adapter creates the row layout and maps the data to the views in the layout.
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.news_list_row, parent, false);

        TextView headlineTv = (TextView)rowView.findViewById(R.id.nlr_newsItem_headline_Tv);
        TextView bodyTv = (TextView)rowView.findViewById(R.id.nlr_newsItem_body_Tv);
        ImageView picIv = (ImageView)rowView.findViewById(R.id.nlr_newsItem_pic_Iv);


        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
        headlineTv.setTypeface(typeface); bodyTv.setTypeface(typeface);

        headlineTv.setText(headline[position]);
        bodyTv.setText(body[position]);
        picIv.setImageResource(imageId[position]);

        return rowView;
    }
}
