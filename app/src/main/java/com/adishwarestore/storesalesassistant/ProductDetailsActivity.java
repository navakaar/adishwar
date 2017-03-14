package com.adishwarestore.storesalesassistant;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by dhiraj on 31-Dec-16.
 */

public class ProductDetailsActivity extends Activity implements ProductSearchFragment.OnProductSearchButtonClickedInterface, ProductSearchResultsFragment.onProductSelectedInterface {
    // Variable to store list of products matching search criteria
    Product[] productList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        // Replace the first fragment into the fragment_container placeholder
        if (findViewById(R.id.fragment_container) != null) {

            Fragment productSearchFragment = new ProductSearchFragment();
            productSearchFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            //getFragmentManager().beginTransaction().add(R.id.fragment_container, productSearchFragment, "SearchProduct").addToBackStack("SearchProduct").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
            // // GYAAN: If you have more than one fragment been used in the activity or even if you have only one
            // // fragment then the first fragment should not have addToBackStack defined. Since this allows back navigation
            // // and prior to this fragment the empty activity layout will be displayed.
            // // fragmentmaganer.addToBackStack() // dont include this for your first fragment.
            getFragmentManager().beginTransaction().add(R.id.fragment_container, productSearchFragment, "SearchProduct").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }

    // Callback method from ProductSearchFragment
    @Override
    public void onProductSearchButtonClicked(String prodcat, String prod, String prodcode) {
        //Toast.makeText(ProductDetailsActivity.this, "[SUCCESS] Category = " + prodcat + ", Product = " + prod + ", Code = " + prodcode, Toast.LENGTH_SHORT).show();

        String[] searchParams = {prodcat, prod, prodcode};

        // These parameters to be sent to the DB for retrieving product details (description, price, offers)
        new ProductSearchTask().execute(searchParams);

        return;
    }


    @Override
    public void onProductSelected(Product p) {
        //Toast.makeText(ProductDetailsActivity.this, "Product Selected = " + p.getProductTitle(), Toast.LENGTH_SHORT).show();

        // Show product details
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(getIntent().getExtras());
        getIntent().putExtra("Product", p);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction().add(R.id.fragment_container, productDetailsFragment, "ProductDetails").addToBackStack("ProductDetails").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

    }

    // Background Task to Retrieve Product Details (Desc, Price, Offers) from Database
    // // GYAAN:: An asynchronous task is defined by 3 generic types, called Params, Progress and Result, and
    // // 4 steps, called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.
    // // // Params, the type of the parameters sent to the task upon execution
    // // // Progress, the type of the progress units published during the background computation.
    // // // Result, the type of the result of the background computation.
    private class ProductSearchTask extends AsyncTask<String, Integer, String> {
        ProgressBar search_progressbar;

        @Override
        protected void onPreExecute() {
            search_progressbar = (ProgressBar)findViewById(R.id.progress_Pb);

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            OutputStream outputStream = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            try {
                URL url = new URL("http://navkar.bitnamiapp.com/Adishwar/backend/ProdSearch.php");


                //
                // Send POST Request
                //
                JSONObject prodSearchInputsJSON = new JSONObject();
                prodSearchInputsJSON.put("prodcat", params[0]);
                prodSearchInputsJSON.put("prod", params[1]);
                prodSearchInputsJSON.put("prodcode", params[2]);
                Log.d(getClass().getName(), "JSON to be Sent === " + prodSearchInputsJSON.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(15000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true); /* HttpURLConnection uses the GET method by default. It will use POST if setDoOutput(true) has been called */
                urlConnection.setFixedLengthStreamingMode(prodSearchInputsJSON.toString().getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                urlConnection.connect();


                outputStream = new BufferedOutputStream(urlConnection.getOutputStream()); // urlConnection.getOutputStream(): returns output stream that writes to this connection
                outputStream.write(prodSearchInputsJSON.toString().getBytes());

                outputStream.flush();
                outputStream.close();

                //
                // Read HTTP Response
                //
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {

                    inputStream = urlConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {

                        sb.append(line + "\n");
                    }

                    bufferedReader.close();
                    //return sb.toString();

                    if (sb.length() == 0) {
                        // Stream was empty. No point in parsing.
                        return "ERROR | Some Error Occurred in Product Search! (Blank Response)";
                    }
                    else
                        Log.d(getClass().getName(), "Response JSON == " + sb.toString());


                    JSONObject responseJson = null;
                    try {
                        // Read the PHP Response JSON
                        responseJson = new JSONObject(sb.toString());

                        if (responseJson.getString("error_status").equals("true")) // Status (error == true) received
                        {
                            return "ERROR | Some Error Occurred in Product Search!";
                        } else if (responseJson.getString("error_status").equals("false") && responseJson.getInt("product_match_count")==0) {
                            return "No Product Matched for given Criteria!";
                        } else { // retrieve matched products
                            JSONArray jsonProdList = responseJson.getJSONArray("products_matched");
                            int productcount = jsonProdList.length();

                            // initialize array with given count
                            productList = new Product[productcount];

                            for (int i=0; i<productcount; i++) {
                                JSONObject jsonProduct = jsonProdList.getJSONObject(i);
                                String prodcat = jsonProduct.getString("cat"); String prodcode = jsonProduct.getString("code");
                                String prodtype = jsonProduct.getString("prodtype"); String prodsubtype = jsonProduct.getString("prodsubtype");
                                String brand = jsonProduct.getString("brand"); String desc = jsonProduct.getString("desc");
                                String off_cd = jsonProduct.getString("offers-cd"); String off_oth = jsonProduct.getString("offers-others");
                                int mrp = jsonProduct.getInt("mrp"); int op = jsonProduct.getInt("op");
                                Product product = new Product(prodcat, prodtype, prodsubtype, prodcode, brand, desc, mrp, op, off_cd, off_oth);

                                product.print();
                                productList[i] = product;
                            }
                            return "Product(s) matching given Criteria = " + productcount;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    return new String("ERROR | Some Error Occured in Product Search! (" + responseCode + ")");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                urlConnection.disconnect();
            }

            return "false : Some Issue in ProductSearchTask";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            search_progressbar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String response) {
            search_progressbar.setVisibility(View.GONE);
            Log.d(getClass().getName(), "Response == " + response);
            //Toast.makeText(ProductDetailsActivity.this, "Search Response = " + response, Toast.LENGTH_SHORT).show();

            if (response.contains("Product(s) matching given Criteria")) {
                CallOnTaskCompletion();
            } else
                Toast.makeText(ProductDetailsActivity.this, "[Error] No Matches Found! Please retry search with different inputs", Toast.LENGTH_LONG).show();
            //super.onPostExecute(response);
        }

        private void CallOnTaskCompletion() {
            // Show list of products matching search criteria
            ProductSearchResultsFragment productSearchResultsFragment = new ProductSearchResultsFragment();
            productSearchResultsFragment.setArguments(getIntent().getExtras());
            // WHAT IS THE NEED TO DO THIS COUNT, IT SHOULD NOT REACHED HERE IF THE COUNT == 0?????
            // CHECK AND FIX?????
            getIntent().putExtra("ProductList", productList);
            if (productList == null) {
                getIntent().putExtra("ProductCount", 0);
            } else {
                getIntent().putExtra("ProductCount", productList.length);
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction().add(R.id.fragment_container, productSearchResultsFragment, "ProductSearchResults").addToBackStack("ProductSearchResults").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        }
    }
}
