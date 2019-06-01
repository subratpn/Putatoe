package com.putatoe.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.putatoe.app.R;
import com.putatoe.app.adapter.ProductAdapter;
import com.putatoe.app.pojo.Product;
import com.putatoe.app.pojo.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.paperdb.Paper;

public class ProductActivity extends AppCompatActivity {

    private static Service service;
    private TextView noProductView;
    private ShimmerFrameLayout mShimmerViewContainer;
    private ActionBar actionBar;
    private View actionView;
    private Handler handler;
    private String token;
    private boolean isMultipleProductPurchaseSupportedService;
    private Button goToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        noProductView = (TextView) findViewById(R.id.no_product_view);
        noProductView.setVisibility(View.INVISIBLE);
        goToCartButton = (Button) findViewById(R.id.goToCartButton);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Paper.init(ProductActivity.this);
                token = Paper.book().read("token");
                getProducts();
            }
        });

    }


    private void getProducts(){

        Intent i = getIntent();
        if(i!=null && i.getExtras()!=null){

            service = (Service) i.getSerializableExtra("service");
            isMultipleProductPurchaseSupportedService = service.getIsMultipleProductPurchaseSupported();
            setCustomTitle(service.getName().toUpperCase());

        }

        String serviceURL = getString(R.string.domain_name)+getString(R.string.product_service_url)+service.getId();
        Log.e("Product URL",serviceURL);
        AndroidNetworking.get(serviceURL)
                .addHeaders("Authorization",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Product Response",response+"");
                        try {
                            parseProducts(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Product Error Response",anError.getErrorDetail()+"");
                    }});



    }

    private void parseProducts(JSONArray response) throws JSONException {

        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);

        if(response.length()  == 0){
            noProductView.setVisibility(View.VISIBLE);
            return;
        }
        ArrayList<Product> products = new ArrayList<>();
        for(int i = 0 ; i < response.length() ; i++){
            JSONObject productObject = response.getJSONObject(i);
            products.add(new Product(productObject.getInt("id"),productObject.getString("name"),productObject.getString("price"),productObject.getString("quantity"),
                    productObject.getString("discount"),productObject.getString("preDiscountPrice"),0));
        }

        if(isMultipleProductPurchaseSupportedService){
            goToCartButton.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.product_recycler);
        ProductAdapter adapter = new ProductAdapter(service.getName(),service.getImage(),products,this,service.getIsMultipleProductPurchaseSupported());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        mShimmerViewContainer.startShimmerAnimation();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    private void setCustomTitle(String text){

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionView = inflator.inflate(R.layout.custom_other_actionbar_layout, null);
        actionBar.setCustomView(actionView);

        TextView titleView = (TextView) actionView.findViewById(R.id.custom_other_actionbar_title_view);
        titleView.setText(text);

    }
}
