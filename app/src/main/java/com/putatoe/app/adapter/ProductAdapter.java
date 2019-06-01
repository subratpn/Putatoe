package com.putatoe.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putatoe.app.R;
import com.putatoe.app.activity.CartActivity;
import com.putatoe.app.activity.ProductActivity;
import com.putatoe.app.pojo.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    ArrayList<Product> products;
    Context context;
    Typeface typeface;
    String serviceName;
    String serviceImage;
    boolean isMultipleProductPurchaseSupportedService;

    public ProductAdapter(String serviceName, String serviceImage,ArrayList<Product> products, Context context,boolean isMultipleProductPurchaseSupportedService) {
        this.products = products;
        this.context = context;
        this.serviceName = serviceName;
        this.serviceImage = serviceImage;
        this.isMultipleProductPurchaseSupportedService = isMultipleProductPurchaseSupportedService;
        typeface = Typeface.createFromAsset(context.getAssets(),"comforta.ttf");
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.product_layout, viewGroup, false);
        ProductAdapter.ViewHolder viewHolder = new ProductAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Product product = products.get(i);
        viewHolder.productName.setText(product.getName());
        viewHolder.productRate.setText("Rs. "+ product.getPrice());
        viewHolder.productQuantity.setText(product.getQuantity());

        if(isMultipleProductPurchaseSupportedService) {
            viewHolder.productBook.setVisibility(View.GONE);
            viewHolder.productMinusView.setVisibility(View.VISIBLE);
            viewHolder.productPlusView.setVisibility(View.VISIBLE);
            viewHolder.productBuyQuantity.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.productBook.setVisibility(View.VISIBLE);
            viewHolder.productMinusView.setVisibility(View.GONE);
            viewHolder.productPlusView.setVisibility(View.GONE);
            viewHolder.productBuyQuantity.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;
        public TextView productRate;
        public TextView productQuantity;
        public Button productBook;
        public TextView productMinusView;
        public TextView productPlusView;
        public TextView productBuyQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = (TextView) itemView.findViewById(R.id.product_name);
            productRate = (TextView) itemView.findViewById(R.id.product_rate);
            productBook = (Button) itemView.findViewById(R.id.product_add);
            productQuantity = (TextView) itemView.findViewById(R.id.product_quantity);
            productMinusView = (TextView) itemView.findViewById(R.id.product_minus);
            productPlusView = (TextView) itemView.findViewById(R.id.product_plus);
            productBuyQuantity = (TextView) itemView.findViewById(R.id.product_buy_quantity);

            productBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product p = products.get(getAdapterPosition());
                    Intent i = new Intent(context, CartActivity.class);
                    i.putExtra("product",p);
                    i.putExtra("service",serviceName);
                    context.startActivity(i);
                }
            });
        }
    }


}
