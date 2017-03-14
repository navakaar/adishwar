package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by dhiraj on 05-Jan-17.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {
    List<Product> productList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, cat, mrp;


        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.plr_prodname_Tv);
            cat = (TextView) view.findViewById(R.id.plr_proddesc_Tv);
            mrp = (TextView) view.findViewById(R.id.plr_mrp_Tv);

            // Initialize new typeface
            Typeface typeface = Typeface.createFromAsset(view.getContext().getAssets(),
                    String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
            name.setTypeface(typeface); cat.setTypeface(typeface); mrp.setTypeface(typeface);
        }
    }

    ProductsAdapter(List<Product> productsList) {
        this.productList = productsList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Product product = productList.get(position);

        holder.name.setText(product.getProductTitle());
        holder.cat.setText(product.getBriefDesc());
        DecimalFormat formatter = new DecimalFormat("##,##,##,###");
        String formatted_prodmrp = formatter.format(product.getMRP())+".00";
        holder.mrp.setText("MRP: ₹" + formatted_prodmrp);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
