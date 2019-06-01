package com.putatoe.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.putatoe.app.R;
import com.putatoe.app.adapter.ReviewAdapter;
import com.putatoe.app.adapter.ServiceAdapter;
import com.putatoe.app.pojo.Review;
import com.putatoe.app.pojo.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {

    private int LOCATION_REQUEST_CODE = 200;
    private LocationManager locationManager;
    private LinearLayout rootLayout;
    private ProgressDialog progressDialog;
    private TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Paper.init(this);

        rootLayout = (LinearLayout) findViewById(R.id.home_root_layout);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationView = (TextView)findViewById(R.id.appbar_location);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Adding Slider to View
        addSlider();

        Intent i = getIntent();
        if(i != null && i.getExtras() != null){

            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) locationView.getLayoutParams();
            params.width = getResources().getDimensionPixelSize(R.dimen.location_text_view_width);
            locationView.setLayoutParams(params);
            locationView.setText(i.getExtras().getString("location"));
            addServices();

        }
        else {
            startLocationUpdates();
        }

    }

    private void addServices() {


        String serviceURL = getString(R.string.domain_name)+getString(R.string.service_city_url)+"1";
        Log.e("Service URL",serviceURL);
        final ProgressDialog serviceProgressDialog = new ProgressDialog(this);
        serviceProgressDialog.setMessage("Retrieving Services For You");
        serviceProgressDialog.show();
        serviceProgressDialog.setCancelable(false);
        String token = Paper.book().read("token");
        AndroidNetworking.get(serviceURL)
                .addHeaders("Authorization",token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Service Response",response+"");
                        serviceProgressDialog.dismiss();
                        try {
                            parseServiceResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Service Response",anError.getErrorDetail()+"");
                        serviceProgressDialog.dismiss();
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


        RecyclerView recyclerGridView = (RecyclerView) findViewById(R.id.service_grid_recycler_view);
        ServiceAdapter adapter = new ServiceAdapter(serviceArrayList,this,R.layout.service_grid_layout);
        recyclerGridView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));
        recyclerGridView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerGridView.setLayoutManager(new GridLayoutManager(this,4));
        recyclerGridView.setAdapter(adapter);


        addReviews();


    }

    private void addReviews() {

        ArrayList<Review> reviews = new ArrayList<>();

        reviews.add(new Review(1,"I am very happy with the carpenter.He was very Punctual and Professional.","Priyanka Dey","4.2"));
        reviews.add(new Review(1,"Plumber had a good knowledge of the issue and charged the minimal amount for the service.","Pritam Gupta","4.5"));
        reviews.add(new Review(1,"Fantastic Response from the team when i was looking for an electrician urgently.","Shubham Rana","4.1"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.customer_reviews_recycler_view);
        ReviewAdapter adapter = new ReviewAdapter(reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
    }

    private void addSlider() {

       /* Slider slider = findViewById(R.id.slider);

        //create list of slides
        List<Slide> slideList = new ArrayList<>();
        slideList.add(new Slide(0,"https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_9bf537c0.png" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(1,"https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_27a76c00.png" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));
        slideList.add(new Slide(2,"https://res.cloudinary.com/urbanclap/image/upload/q_auto,f_auto,fl_progressive:steep/categories/category_v2/category_308a76f0.jpeg" , getResources().getDimensionPixelSize(R.dimen.slider_image_corner)));


        //add slides to slider
        slider.addSlides(slideList);*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {

            Needle.onBackgroundThread().execute(new UiRelatedTask<Boolean>() {
                @Override
                protected Boolean doWork() {
                    Paper.init(HomeActivity.this);
                    Paper.book().delete("token");
                    return true;
                }

                @Override
                protected void thenDoUiRelatedWork(final Boolean result) {
                    startActivity(new Intent(HomeActivity.this,MobileLoginActivity.class));
                    finish();
                }
            });

        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(this,ContactActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void navigateToLocationActivty(View view) {

        Intent i = new Intent(this,LocationActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {
        findCompleteAddress(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case 200: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                            Snackbar snackbar = Snackbar
                                    .make(rootLayout, "Location Services are Disabled.Please Enable and Retry", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            return;
                        }

                        if(progressDialog == null){
                            progressDialog = new ProgressDialog(HomeActivity.this);
                            progressDialog.setMessage("Fetching Location");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
                    }

                }
            }
        }
    }

    private void findCompleteAddress(final Location location) {

        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Geocoder geocoder = new Geocoder(HomeActivity.this, Locale.getDefault());
                List<Address> addressList = null;
                String completeAddress = null;

                try {
                    addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addressList != null) {
                    String address = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addressList.get(0).getLocality();
                    String state = addressList.get(0).getAdminArea();
                    String country = addressList.get(0).getCountryName();
                    String postalCode = addressList.get(0).getPostalCode();
                    completeAddress = address + "," + city + "," + state + "," + country + "," + postalCode;
                }

                return completeAddress;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {

                if(result==null){
                    Snackbar snackbar = Snackbar
                            .make(rootLayout, "Poor Network Connection...Try Again", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else {
                    Log.e("Address", result);
                    if(progressDialog!=null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    locationManager.removeUpdates(HomeActivity.this);
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) locationView.getLayoutParams();
                    params.width = getResources().getDimensionPixelSize(R.dimen.location_text_view_width);
                    locationView.setLayoutParams(params);
                    locationView.setText(result);
                    // Add Services
                    addServices();
                }
            }
        });


    }


    public void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
        else {

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                Snackbar snackbar = Snackbar
                        .make(rootLayout, "Location Services are Disabled.Please Enable and Retry", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }

            if(progressDialog == null){
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Fetching Location");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);

        }
    }

}
