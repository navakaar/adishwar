package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by dhiraj on 05-Jan-17.
 */

public class ProductSearchResultsFragment extends Fragment {
    private List<Product> productList = new ArrayList<>();
    private onProductSelectedInterface mCallback;

    public ProductSearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_searchresults, container, false);


        TextView title_Tv = (TextView)v.findViewById(R.id.fpsr_title_Tv);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                String.format(Locale.US, "fonts/%s", "centurygothic.ttf"));
        title_Tv.setTypeface(typeface);

        RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        Product[] productArray = (Product[])getActivity().getIntent().getSerializableExtra("ProductList");
        productList = Arrays.asList(productArray);
        ProductsAdapter mAdapter = new ProductsAdapter(productList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new ProdSrchResultsRecyclerTouchListener(v.getContext(), recyclerView, new ProdSrchResultsRecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product product = productList.get(position);
                //Toast.makeText(view.getContext(), product.getProductTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                mCallback.onProductSelected(product);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return v;
    }

    public interface onProductSelectedInterface {
        void onProductSelected(Product p);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ProductSearchResultsFragment.onProductSelectedInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onProductSelectedInterface");
        }
    }
}
