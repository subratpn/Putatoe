package com.putatoe.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putatoe.app.R;
import com.putatoe.app.activity.HomeActivity;
import com.putatoe.app.activity.ProductActivity;
import com.putatoe.app.pojo.Service;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {


    ArrayList<Service> serviceList;
    Context context;
    Typeface typeface;private
    int layout;
    final static int FADE_DURATION = 1500;


    public ServiceAdapter(ArrayList<Service> serviceList, Context context, int layout) {
        this.serviceList = serviceList;
        this.context = context;
        this.layout = layout;
        typeface = Typeface.createFromAsset(context.getAssets(),"comforta.ttf");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Service service = serviceList.get(i);
        Picasso.with(context).load(service.getImage()).fit().into(viewHolder.serviceImage);
        viewHolder.serviceName.setText(service.getName());
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView serviceImage;
        public TextView serviceName;
        public LinearLayout serviceLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = (ImageView) itemView.findViewById(R.id.service_image);
            serviceName = (TextView) itemView.findViewById(R.id.service_name);
            serviceLayout = (LinearLayout) itemView.findViewById(R.id.service_linear_layout);
            //serviceName.setTypeface(typeface);
            serviceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ProductActivity.class);
                    i.putExtra("service",serviceList.get(getAdapterPosition()));
                    context.startActivity(i);
                }
            });
        }
    }


}
