package com.adishwarestore.storesalesassistant;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;

import com.adishwarestore.storesalesassistant.UrlImageView;
/**
 * Created by dhiraj on 03-Jan-17.
 */

public class ProductDetailsFragment extends Fragment {
    Product[] productList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_details, container, false);

        // Retrieve product details from Activity
        Product product = (Product)getActivity().getIntent().getSerializableExtra("Product");
        if (product == null ) {
            Log.d(getClass().getName(), "Product received is NULL!!!");
            return v;
        }

        String prodcat = product.category, prod_type = product.product_type, prod_subtype = product.product_subtype, prodcode = product.code;
        String prodbrand = product.brand, proddesc = product.desc;
        int prodmrp = product.mrp, prodfp = product.offer_price;
        String prodoff_cd = product.offer_cd, prodoff_oth = product.offer_other;


        // Initialize new typeface
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));

        // Set Product Title
        TextView prodnameTv = (TextView)v.findViewById(R.id.product_name_Tv);

        String normalBefore= prodcat + "> " + prod_type + "> " + prod_subtype + "> \n";
        String normalBOLD=  prodbrand + " - " + prodcode;
        String finalString= normalBefore+normalBOLD;
        Spannable sb = new SpannableString( finalString );
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(normalBOLD), finalString.indexOf(normalBOLD)+ normalBOLD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        prodnameTv.setText(sb); prodnameTv.setTypeface(typeface);

        try {
            UrlImageView prod1UrlImageView = (UrlImageView)v.findViewById(R.id.prodImg1_Uiv);
            UrlImageView prod2UrlImageView = (UrlImageView)v.findViewById(R.id.prodImg2_Uiv);

            // replace any ' ' with '%20'
            String prodsubtype_forURL = prod_subtype.replace(" ", "%20");
            // replace any '/' with ''
            String prodcode_forURL = prodcode.replace("/", "");


            String imgURLStr = "http://navkar.bitnamiapp.com/Adishwar/images/PRODUCTS/"
                                                    + prodcat.toUpperCase() + "/"
                                                    + prod_type.toUpperCase() + "/"
                                                    + prodsubtype_forURL.toUpperCase() + "/"
                                                    + prodbrand.toUpperCase() + "-"+ prodcode_forURL.toUpperCase();
            //"http://navkar.bitnamiapp.com/Adishwar/images/CE/TELEVISION/lg43uf640t/img01.jpg"
            URL img1Url = new URL(imgURLStr + "/img01.jpg");    URL img2Url = new URL(imgURLStr + "/img02.jpg");
            Log.d("ProductDetailsFrag", "ImgURLStr = " + imgURLStr);
            prod1UrlImageView.setImageURL(img1Url);             prod2UrlImageView.setImageURL(img2Url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Set Product Desc
        TextView proddescTv = (TextView)v.findViewById(R.id.product_desc_Tv);

        normalBOLD=  "Product Details: ";
        String normalAfter= proddesc;
        finalString= normalBOLD+normalAfter;
        sb = new SpannableString( finalString );
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(normalBOLD), finalString.indexOf(normalBOLD)+ normalBOLD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        proddescTv.setText(sb); proddescTv.setTypeface(typeface);
        //proddescTv.setText(Html.fromHtml("<B>Key Features: </B><BR>") + proddesc);


        // Format Total Liters Number & set to TextView
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String formatted_prodmrp = formatter.format(prodmrp); String formatted_prodfp = formatter.format(prodfp);
        String formatted_proddisc = formatter.format(prodmrp - prodfp);

        // Set Product MRP
        normalBOLD=  "MRP: ";
        normalAfter= formatted_prodmrp + ".00";
        finalString= normalBOLD+normalAfter;
        sb = new SpannableString( finalString );
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(normalBOLD), finalString.indexOf(normalBOLD)+ normalBOLD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        TextView prodmrpTv = (TextView)v.findViewById(R.id.price_mrp_Tv);
        prodmrpTv.setText(sb); prodmrpTv.setTypeface(typeface);

        // Set Product FP
        normalBOLD=  "Offer Price: ";
        normalAfter= formatted_prodfp + ".00";
        finalString= normalBOLD+normalAfter;
        sb = new SpannableString( finalString );
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), finalString.indexOf(normalBOLD), finalString.indexOf(normalBOLD)+ normalBOLD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        TextView prodfpTv = (TextView)v.findViewById(R.id.price_fp_Tv);
        prodfpTv.setText(sb); prodfpTv.setTypeface(typeface);

        // Set Product Discount
        normalBOLD=  "Adishwar Discount: ";
        normalAfter= formatted_proddisc + ".00";
        finalString= normalBOLD+normalAfter;
        sb = new SpannableString( finalString );
        sb.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), finalString.indexOf(normalBOLD), finalString.indexOf(normalBOLD)+ normalBOLD.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
        TextView proddiscTv = (TextView)v.findViewById(R.id.price_disc_Tv);
        proddiscTv.setText(sb); proddiscTv.setTypeface(typeface);

        // Set Product Offers (CD & Others)
        TextView prodOffTv = (TextView)v.findViewById(R.id.product_off_Tv);
        prodOffTv.setTypeface(typeface, Typeface.BOLD);
        TextView prodOffCDTv = (TextView)v.findViewById(R.id.product_off_CD_Tv);
        prodOffCDTv.setText("• CD Code: " + prodoff_cd); prodOffCDTv.setTypeface(typeface);
        TextView prodOffOthTv = (TextView)v.findViewById(R.id.product_off_OTH_Tv);
        prodOffOthTv.setText("• Additional Offers: " + prodoff_oth); prodOffOthTv.setTypeface(typeface);

        return v;
    }
}
