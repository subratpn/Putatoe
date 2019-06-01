package com.putatoe.app.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.putatoe.app.R;
import com.putatoe.app.pojo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;
import okhttp3.Headers;
import okhttp3.Response;

public class MobileRegisterActivity extends AppCompatActivity {


    private AppCompatEditText mobileInput;
    private AppCompatEditText passwordInput;
    private RelativeLayout rootLayout;
    private static final String smsurl = "https://www.way2sms.com/api/v1/sendCampaign";
    private static final String apiKey = "O0KS1TOFC4OA298RMJ7201VWAM84A1YH";
    private static final String secretKey = "Y0LNACTJPO5MPL8R";
    private static final String useType = "prod";
    private static final String TAG = "SMS";
    private static final String senderID = "LazyIn";
    private ActionBar actionBar;
    private View actionView;
    private ProgressBar registerLoader;
    private TextView mobileInputError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_register);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setCustomTitle("REGISTER");

        mobileInput = (AppCompatEditText) findViewById(R.id.mobile_input);
        passwordInput = (AppCompatEditText) findViewById(R.id.password_input);
        rootLayout = (RelativeLayout) findViewById(R.id.root_register_layout);
        registerLoader = (ProgressBar) findViewById(R.id.register_loader);
        mobileInputError = (TextView) findViewById(R.id.mobile_input_error);
        mobileInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mobileInputError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void performRegistartion() throws JSONException {

        final JSONObject registrationPostData = new JSONObject();
        final String username = mobileInput.getText().toString();
        final String password = passwordInput.getText().toString();
        registrationPostData.put("username",username);
        registrationPostData.put("password",password);

        registerLoader.setVisibility(View.VISIBLE);
        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.registration_url))
                .addJSONObjectBody(registrationPostData)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Log.e("Registration Response",response.toString());
                        int responseCode = response.code();
                        if(responseCode == 200){
                            try {
                                performLogin(username,password);
                            } catch (JSONException e) {
                                registerLoader.setVisibility(View.INVISIBLE);
                                e.printStackTrace();
                            }

                        }else {

                            registerLoader.setVisibility(View.INVISIBLE);
                            Snackbar snackbar = Snackbar
                                    .make(rootLayout, "Details Already Exist With Us", Snackbar.LENGTH_SHORT);
                            snackbar.show();

                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.e("Registration Error",anError.getErrorDetail());
                        Snackbar snackbar = Snackbar
                                .make(rootLayout,anError.getErrorDetail(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });



    }

    public void saveUserDetailsInMemory(final String username, String token) {
        AndroidNetworking.get(getString(R.string.domain_name)+getString(R.string.user_details_by_mobile)+username)
                .setPriority(Priority.MEDIUM)
                .addHeaders("Authorization",token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userid = String.valueOf(response.getInt("id"));
                            User user = new User(userid,username,null);
                            Paper.book().write("user",user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }

    private void verifyOTP(final String result) {


        registerLoader.setVisibility(View.INVISIBLE);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.otp_layout,null);
        final AppCompatEditText otpBox = (AppCompatEditText) viewInflated.findViewById(R.id.otp_input);
        final TextView otpBoxErrorView = (TextView) viewInflated.findViewById(R.id.otp_error_text);
        alertBuilder.setView(viewInflated);
        alertBuilder.setCancelable(false);
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.setPositiveButton("Confirm",null);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        {

                            Log.e("Current OTP",result);
                            if(result.equals(otpBox.getText().toString())){
                                Log.e("OTP Flow","Correct");
                                dialog.dismiss();
                                try {
                                    performRegistartion();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Log.e("OTP Flow","Wrong");
                                otpBox.setText("");
                                otpBoxErrorView.setText("Invalid OTP");
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void performLogin(final String username, final String password) throws JSONException {

        JSONObject loginPostData = new JSONObject();
        loginPostData.put("username",username);
        loginPostData.put("password",password);

        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.login_url))
                .addJSONObjectBody(loginPostData)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        registerLoader.setVisibility(View.INVISIBLE);
                        Log.e("Login Response",response.toString());
                        Headers headers = response.headers();
                        String token = headers.get("Authorization");
                        saveUserDetailsInMemory(username,token);
                        Paper.book().write("token",token);
                        Intent i = new Intent(MobileRegisterActivity.this, BasicHomeActivity.class);
                        startActivity(i);
                        ActivityCompat.finishAffinity(MobileRegisterActivity.this);
                    }

                    @Override
                    public void onError(ANError anError) {
                        registerLoader.setVisibility(View.INVISIBLE);
                        Log.e("Login Error",anError.getErrorDetail());
                        Snackbar snackbar = Snackbar
                                .make(rootLayout,anError.getErrorDetail(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });

    }

    public static String sendCampaign(String phone, String message) {

        Log.e("Inside Sending SMS","SMS");
        StringBuilder content = new StringBuilder();
        try{
            // construct data
            JSONObject urlParameters = new JSONObject();
            urlParameters.put("apikey",apiKey);
            urlParameters.put("secret",secretKey);
            urlParameters.put("usetype",useType);
            urlParameters.put("phone", phone);
            urlParameters.put("message", URLEncoder.encode(message,"UTF-8"));
            urlParameters.put("senderid", senderID);
            URL obj = new URL(smsurl);
            // send data
            HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            DataOutputStream wr = new DataOutputStream(httpConnection.getOutputStream());
            wr.write(urlParameters.toString().getBytes());

            // get the response
            BufferedReader bufferedReader = null;
            if (httpConnection.getResponseCode() == 200) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
            }

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();
        }catch(Exception ex){ }
        return content.toString();
    }


    public void startRegistartion(View view) {


        final String mobile = mobileInput.getText().toString();
        if(mobile.length() != 10){
            mobileInputError.setVisibility(View.VISIBLE);
        }else {
            registerLoader.setVisibility(View.VISIBLE);
            Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
                @Override
                protected String doWork() {

                    String generatedOTP = "" + ((int) (Math.random() * 9000) + 1000);
                    sendCampaign(mobile, "Welcome to Putatoe.\nOTP : " + generatedOTP);
                    registerLoader.setVisibility(View.INVISIBLE);
                    return generatedOTP;
                }

                @Override
                protected void thenDoUiRelatedWork(final String result) {

                    verifyOTP(result);

                }
            });

        }

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

}


