package com.putatoe.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.putatoe.app.R;
import com.putatoe.app.fragment.OfferFragment;
import com.putatoe.app.pojo.Offer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.ViewHolder>  {

    private ArrayList<Offer> offerList;
    private Context context;

    public OfferAdapter(ArrayList<Offer> offerList, Context context) {
        this.offerList = offerList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.offer_layout, viewGroup, false);
        OfferAdapter.ViewHolder viewHolder = new OfferAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Offer offer = offerList.get(i);
        Picasso.with(context).load(offer.getImage()).fit().into(viewHolder.offerImage);

    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView offerImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage = (ImageView) itemView.findViewById(R.id.offer_imageview);
        }


    }
}
