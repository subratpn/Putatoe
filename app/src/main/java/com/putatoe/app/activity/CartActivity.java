package com.putatoe.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putatoe.app.R;
import com.putatoe.app.adapter.BookingDateAdapter;
import com.putatoe.app.adapter.BookingTimeAdapter;
import com.putatoe.app.adapter.ReviewAdapter;
import com.putatoe.app.pojo.BookingDate;
import com.putatoe.app.pojo.BookingTime;
import com.putatoe.app.pojo.Product;
import com.putatoe.app.pojo.Review;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CartActivity extends AppCompatActivity {

    private Button button;
    private TextView productName;
    private TextView productQuantity;
    private TextView productRate;
    private TextView totalPayableView;
    private TextView pickTimeSlotView;
    private TextView cartServiceName;
    private TextView addressHeading;
    private TextView addressDetails;
    private DecimalFormat df = new DecimalFormat("####0.00");
    private static Calendar calendar = Calendar.getInstance();
    private static Product orderedProduct;
    private ActionBar actionBar;
    private View actionView;
    private String service;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setCustomTitle("Cart".toUpperCase());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        button = (Button) findViewById(R.id.continue_to_payment_button);
        productName = (TextView) findViewById(R.id.product_name);
        productQuantity = (TextView) findViewById(R.id.product_quantity);
        productRate = (TextView) findViewById(R.id.product_rate);
        totalPayableView = (TextView) findViewById(R.id.product_total_payable);
        pickTimeSlotView = (TextView) findViewById(R.id.pick_timeslot_view);
        cartServiceName = (TextView) findViewById(R.id.cart_service_name);
        addressHeading = (TextView) findViewById(R.id.address_view);
        addressDetails = (TextView) findViewById(R.id.address_details);

        Typeface typeface = Typeface.createFromAsset(getAssets(),"comforta.ttf");
        /*totalPayableView.setTypeface(typeface);
        pickTimeSlotView.setTypeface(typeface);
        cartServiceName.setTypeface(typeface);
        productName.setTypeface(typeface);
        productQuantity.setTypeface(typeface);
        productRate.setTypeface(typeface);
        addressHeading.setTypeface(typeface);
        addressDetails.setTypeface(typeface);*/


        Intent i = getIntent();
        if(i!=null && i.getExtras()!=null){
            orderedProduct = (Product) i.getSerializableExtra("product");
            service = i.getExtras().getString("service");
            productName.setText(orderedProduct.getName());
            productRate.setText("Rs. "+orderedProduct.getPrice());
            productQuantity.setText(orderedProduct.getQuantity());
            cartServiceName.setText(service);
            totalPayableView.setText("Rs. "+orderedProduct.getPrice());
        }

        addBookingDate();
        addBookingTime();


    }

    private void addBookingTime() {


        ArrayList<BookingTime> bookingTimes = new ArrayList<>();
        bookingTimes.add(new BookingTime(1,"10AM - 12PM",false));
        bookingTimes.add(new BookingTime(1,"2PM - 5PM",false));
        bookingTimes.add(new BookingTime(2,"6PM - 9PM",false));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.booking_time_recycler_view);
        BookingTimeAdapter bookingTimeAdapter = new BookingTimeAdapter(bookingTimes,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(bookingTimeAdapter);


    }

    private void addBookingDate() {

        ArrayList<BookingDate> bookingDates = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM");
        Date today = new Date();
        calendar.setTime(today);
        for(int i = 1 ; i <= 5 ; i++){

            String reqDate = simpleDateFormat.format(calendar.getTime());
            bookingDates.add(new BookingDate(i,reqDate,false));
            calendar.add(Calendar.DATE,1);

        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.booking_date_recycler_view);
        BookingDateAdapter bookingDateAdapter = new BookingDateAdapter(bookingDates,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(bookingDateAdapter);
    }

    public void initiatePayment(View view) {
        Intent i = new Intent(this,PaymentActivity.class);
        i.putExtra("product",orderedProduct);
        i.putExtra("service",service);
        startActivity(i);
    }

    public void editAddress(View view) {
        startActivity(new Intent(this,LocationActivity.class));
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

    public void initiateCOD(View view) {

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setView(R.layout.order_placed_cod);
            alertBuilder.setCancelable(false);
            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(CartActivity.this,BasicHomeActivity.class));
                    finishAffinity();
                }
            }, 2500);

    }
}

