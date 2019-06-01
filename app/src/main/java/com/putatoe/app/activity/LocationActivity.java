package com.putatoe.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.putatoe.app.R;

import java.util.Arrays;
import java.util.List;

public class LocationActivity extends AppCompatActivity {


    private AutocompleteSupportFragment autocompleteFragment;
    private static String TAG = LocationActivity.class.getSimpleName();
    private LinearLayout createAddressRootLayout;
    private TextView createAddressTextView;
    private ProgressBar addressLoader;
    private EditText addressOne;
    private EditText addressTwo;
    private ActionBar actionBar;
    private View actionView;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static List<Place.Field> placeFields;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setCustomTitle("Location".toUpperCase());


        createAddressRootLayout = (LinearLayout) findViewById(R.id.address_root_layout);
        createAddressRootLayout.setVisibility(View.GONE);
        createAddressTextView = (TextView) findViewById(R.id.create_address_button);
        addressLoader = (ProgressBar) findViewById(R.id.address_loader);
        addressOne = (EditText) findViewById(R.id.address_one);
        addressTwo = (EditText) findViewById(R.id.address_two);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#607D8B'>LOCATION</font>"));

        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCfLuElocMkNMTyxWvHVYSbfw-d8sEJzko");
        }

        //Fetching Location
        fetchLocationFromGoogle();



    }


    public void createAddress(View view) {

        fetchLocationFromGoogle();
        addressLoader.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!createAddressRootLayout.isShown()) {
                    createAddressRootLayout.setVisibility(View.VISIBLE);
                    createAddressTextView.setVisibility(View.GONE);
                }
                addressLoader.setVisibility(View.INVISIBLE);



            }
        }, 2000);


    }

    public void saveAddress(View view) {

        addressLoader.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(createAddressRootLayout.isShown()) {
                    createAddressRootLayout.setVisibility(View.GONE);
                    createAddressTextView.setVisibility(View.VISIBLE);
                }
                addressLoader.setVisibility(View.INVISIBLE);
            }
        }, 1000);


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

    public void fetchLocationFromGoogle() {

        Log.e("Location","fetch method called");
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, placeFields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                if(!createAddressRootLayout.isShown()){
                    createAddressRootLayout.setVisibility(View.VISIBLE);
                }
                addressTwo.setText(place.getAddress());
                addressTwo.setEnabled(false);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}



