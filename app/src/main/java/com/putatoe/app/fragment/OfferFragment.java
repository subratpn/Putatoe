package com.putatoe.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.putatoe.app.R;
import com.putatoe.app.adapter.OfferAdapter;
import com.putatoe.app.pojo.Offer;

import java.util.ArrayList;

public class OfferFragment extends Fragment {
    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_offer, container, false);
        this.recyclerView = (RecyclerView) v.findViewById(R.id.offer_recyclerview);
        addOffers();
        return v;
    }

    private void addOffers() {
        ArrayList<Offer> offers = new ArrayList();
        offers.add(new Offer(Integer.valueOf(1), "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_9bf537c0.png"));
        offers.add(new Offer(Integer.valueOf(2), "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_27a76c00.png"));
        offers.add(new Offer(Integer.valueOf(3), "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_308a76f0.jpeg"));
        OfferAdapter adapter = new OfferAdapter(offers, getContext());
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        this.recyclerView.setAdapter(adapter);
    }
}
