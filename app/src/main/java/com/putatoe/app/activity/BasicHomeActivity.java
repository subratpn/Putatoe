package com.putatoe.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.putatoe.app.R;
import com.putatoe.app.fragment.HomeFragment;
import com.putatoe.app.fragment.OfferFragment;
import com.putatoe.app.fragment.ProfileFragment;
import com.putatoe.app.fragment.WalletFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import needle.Needle;
import needle.UiRelatedTask;

public class BasicHomeActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private FrameLayout basicHomeFrameLayout;
    private int LOCATION_REQUEST_CODE = 200;
    private LinearLayout rootLayout;
    private ActionBar actionBar;
    private View actionView;
    private ProgressBar locationLoader;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static String TAG = BasicHomeActivity.class.getSimpleName();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(R.id.basic_home_fragment,new HomeFragment());
                    return true;
                case R.id.navigation_offer:
                    replaceFragment(R.id.basic_home_fragment,new OfferFragment());
                    return true;
                case R.id.navigation_profile:
                    replaceFragment(R.id.basic_home_fragment,new ProfileFragment());
                    return true;
                case R.id.navigation_wallet:
                    replaceFragment(R.id.basic_home_fragment,new WalletFragment());
                    return true;
            }
            return false;
        }
    };

    private void replaceFragment(int fragment_layout_id, Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(fragment_layout_id,fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        basicHomeFrameLayout = findViewById(R.id.basic_home_fragment);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        rootLayout = (LinearLayout) findViewById(R.id.basic_home_root_layout);


        //Adding Fragment to Home Activity
        replaceFragment(R.id.basic_home_fragment,new HomeFragment());
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionView = inflator.inflate(R.layout.custom_home_actionbar_layout, null);
        actionBar.setCustomView(actionView);




        locationLoader = (ProgressBar) actionView.findViewById(R.id.location_loader);
        locationLoader.getIndeterminateDrawable().setColorFilter(Color.parseColor("#0097A7"), PorterDuff.Mode.MULTIPLY);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                Log.e("Location Callback","true");

                if (locationResult == null) {
                    Log.e("Found Location","False");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.e("Found Location","True");
                    findCompleteAddress(location);
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            };
        };

        checkFusedLocationSettings();
    }

    private void checkFusedLocationSettings() {

        LocationRequest locationRequest = createLocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("Location Settings","true");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Location Failure",e.getMessage());
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(BasicHomeActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }



    public void startLocationUpdates() {

        Log.e("Starting Location","true");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
            Log.e("Permission Granted","false");
        }
        else {



            Log.e("Permission Granted","true");
            locationLoader.setVisibility(View.VISIBLE);
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    locationCallback,
                    null /* Looper */);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case 200: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationLoader.setVisibility(View.VISIBLE);
                        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                                locationCallback,
                                null /* Looper */);

                    }

                }
            }
        }
    }

    private void findCompleteAddress(final Location location) {

        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override
            protected String doWork() {
                Geocoder geocoder = new Geocoder(BasicHomeActivity.this, Locale.getDefault());
                List<Address> addressList = null;
                String completeAddress = null;

                try {
                    addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (addressList != null) {
                    String address = addressList.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addressList.get(0).getLocality();
                    String state = addressList.get(0).getAdminArea();
                    String country = addressList.get(0).getCountryName();
                    String postalCode = addressList.get(0).getPostalCode();
                    completeAddress = address;
                }

                return completeAddress;
            }

            @Override
            protected void thenDoUiRelatedWork(String result) {

                locationLoader.setVisibility(View.GONE);

                if(result==null){
                   //retryGettingLocation();
                }else {

                    TextView locationView = (TextView) actionView.findViewById(R.id.actionbar_location_view);
                    ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) locationView.getLayoutParams();
                    //params.width = getResources().getDimensionPixelSize(R.dimen.location_text_view_width);
                    //params.height = getResources().getDimensionPixelSize(R.dimen.location_text_view_height);
                    locationView.setLayoutParams(params);
                    locationView.setText(result);

                }
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        locationLoader.setVisibility(View.GONE);

    }

    protected LocationRequest createLocationRequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                startLocationUpdates();
            }
        }
    }


    private void retryGettingLocation(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getString(R.string.location_null_message));
        alertBuilder.setCancelable(false);
        alertBuilder.setNeutralButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                restart();
            }
        });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

    }


    public void restart(){

        Intent intent = new Intent(this, SplashActivity.class);
        this.startActivity(intent);
        this.finishAffinity();

    }


    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
}
