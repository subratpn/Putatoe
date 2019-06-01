package com.putatoe.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putatoe.app.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.app_name);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"comforta.ttf");
        textView.setTypeface(typeface);

        rootLayout = (RelativeLayout) findViewById(R.id.root_splash_layout);
        progressBar = (ProgressBar) findViewById(R.id.splash_progress_bar);
        //progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);



        Needle.onBackgroundThread().execute(new UiRelatedTask<Boolean>() {
            @Override
            protected Boolean doWork() {
                Paper.init(SplashActivity.this);
                Boolean result = Paper.book().contains("token");
                Log.e("token result",result+"");
                return result;
            }

            @Override
            protected void thenDoUiRelatedWork(final Boolean result) {

               if(result){
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           startActivity(new Intent(SplashActivity.this,BasicHomeActivity.class));
                           finish();
                       }
                   }, 2000);

               }else{

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this,MobileLoginActivity.class));
                            finish();
                        }
                    }, 2500);

                }
            }
        });
    }








}
