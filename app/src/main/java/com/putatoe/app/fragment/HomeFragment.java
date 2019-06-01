package com.putatoe.app.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.putatoe.app.R;
import com.putatoe.app.activity.BasicHomeActivity;
import com.putatoe.app.activity.MobileLoginActivity;
import com.putatoe.app.activity.SplashActivity;
import com.putatoe.app.adapter.ServiceAdapter;
import com.putatoe.app.adapter.ViewPagerAdapter;
import com.putatoe.app.application.CustomApplication;
import com.putatoe.app.pojo.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private View v;
    private static String TAG = "HomeFragment";
    private ProgressBar progressBar;
    private View serviceDivider;
    private String token;
    private Handler handler;
    private static Fragment fragment;
    private RecyclerView recyclerGridView;
    private ServiceAdapter adapter;
    private JSONArray serviceResponse;
    private static final String SERVICE_RESPONSE = "service_response";

    public HomeFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.service_loader);
        serviceDivider = (View) v.findViewById(R.id.service_divider);
        serviceDivider.setVisibility(View.INVISIBLE);
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Paper.init(getActivity());
                token = Paper.book().read("token");
                addSliders(v);
                addServices();
            }
        });

        return v;
    }

    private void addSliders(View v) {


        final String[] imageUrls = new String[]{
                "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_9bf537c0.png",
                "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_27a76c00.png",
                "https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_308a76f0.jpeg",
        };


        final ViewPager viewPager = v.findViewById(R.id.view_pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);

    }

    private void addServices() {


        String serviceURL = getString(R.string.domain_name)+getString(R.string.service_city_url)+"1";
        Log.e("Service URL",serviceURL);
        progressBar.setVisibility(View.VISIBLE);
        AndroidNetworking.get(serviceURL)
                .addHeaders("Authorization",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Service Response",response+"");
                        Log.e("Service Success Msg",response+"");
                        try {
                            serviceResponse = response;
                            parseServiceResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        serviceDivider.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Service Response",anError.getErrorDetail()+"");
                        Log.e("Service Error Message",anError.getErrorBody()+"");
                        progressBar.setVisibility(View.GONE);
                    }});

    }

    private void parseServiceResponse(JSONArray response) throws JSONException {


        ArrayList<Service> serviceArrayList = new ArrayList<>();
        JSONArray serviceArray = response.getJSONObject(0).getJSONArray("serviceList");
        int serviceLength = serviceArray.length();

        for(int i=0;i<serviceLength;i++){
            JSONObject serviceObject = serviceArray.getJSONObject(i);
            Service service = new Service(serviceObject.getInt("id"),serviceObject.getString("name"),serviceObject.getString("image"),serviceObject.getBoolean("supportMultipleProductPurchase"));
            serviceArrayList.add(service);
        }


        recyclerGridView = (RecyclerView) v.findViewById(R.id.service_grid_recycler_view);
        adapter = new ServiceAdapter(serviceArrayList,getContext(),R.layout.service_grid_layout);
        /*recyclerGridView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.HORIZONTAL));
        recyclerGridView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));*/
        recyclerGridView.setLayoutManager(new GridLayoutManager(getContext(),4));
        recyclerGridView.setAdapter(adapter);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        AndroidNetworking.forceCancelAll();
    }


}
