package com.putatoe.app.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putatoe.app.R;
import com.putatoe.app.pojo.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {


    ArrayList<Review> reviewArrayList;

    public ReviewAdapter(ArrayList<Review> reviewArrayList) {
        this.reviewArrayList = reviewArrayList;
    }



    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View listItem= layoutInflater.inflate(R.layout.review_layout, viewGroup, false);
        ReviewAdapter.ViewHolder viewHolder = new ReviewAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder viewHolder, int i) {
        Review review = reviewArrayList.get(i);
        viewHolder.reviewDesc.setText(review.getReview());
        viewHolder.reviewUser.setText(review.getCustomerName());
        viewHolder.reviewRating.setText(review.getRating());
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView reviewDesc;
        public TextView reviewUser;
        public TextView reviewRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewDesc = (TextView) itemView.findViewById(R.id.review_content);
            reviewUser = (TextView) itemView.findViewById(R.id.review_user);
            reviewRating = (TextView) itemView.findViewById(R.id.review_rating);
        }

    }
}
