package com.putatoe.app.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
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
import com.androidnetworking.interfaces.StringRequestListener;
import com.github.loadingview.LoadingDialog;
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
import okhttp3.Headers;
import okhttp3.Response;

public class MobileLoginActivity extends AppCompatActivity {

    private AppCompatEditText mobileInput;
    private AppCompatEditText passwordInput;
    private Button mobileLoginButton;
    private static RelativeLayout rootLayout;
    private static final String smsurl = "https://www.way2sms.com/api/v1/sendCampaign";
    private static final String apiKey = "O0KS1TOFC4OA298RMJ7201VWAM84A1YH";
    private static final String secretKey = "Y0LNACTJPO5MPL8R";
    private static final String useType = "prod";
    private static final String TAG = "SMS";
    private static final String senderID = "LazyIn";
    private ActionBar actionBar;
    private View actionView;
    private ProgressBar loginLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_login);
        getSupportActionBar().setTitle("Login");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setCustomTitle("LOGIN");

        mobileInput = (AppCompatEditText) findViewById(R.id.mobile_input);
        passwordInput = (AppCompatEditText) findViewById(R.id.password_input);
        rootLayout = (RelativeLayout) findViewById(R.id.root_login_layout);
        loginLoader = (ProgressBar) findViewById(R.id.login_loader);

    }

    public void performLogin(View view) throws JSONException {


        final String username = mobileInput.getText().toString();
        final String password = passwordInput.getText().toString();

        JSONObject loginPostData = new JSONObject();
        loginPostData.put("username",username);
        loginPostData.put("password",password);


        loginLoader.setVisibility(View.VISIBLE);
        AndroidNetworking.post(getString(R.string.domain_name)+getString(R.string.login_url))
                .addJSONObjectBody(loginPostData)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        loginLoader.setVisibility(View.INVISIBLE);
                        Log.e("Login Response",response.toString());
                        Headers headers = response.headers();
                        String token = headers.get("Authorization");
                        if(token == null){
                            Snackbar snackbar = Snackbar
                                    .make(rootLayout, "Invalid Credentials", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }else {
                            Paper.book().write("token",token);
                            saveUserDetailsInMemory(username,token);
                            Intent i = new Intent(MobileLoginActivity.this, BasicHomeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loginLoader.setVisibility(View.INVISIBLE);
                        Log.e("Login Error",anError.getErrorDetail());
                        Snackbar snackbar = Snackbar
                                .make(rootLayout,anError.getErrorDetail(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });





    }

    public void navigateToRegisterActivity(View view) {

        startActivity(new Intent(this, MobileRegisterActivity.class));

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

    public void saveUserDetailsInMemory(final String username, String token) {

        Log.e("Writing User","true"+getString(R.string.domain_name)+getString(R.string.user_details_by_mobile)+username);
        AndroidNetworking.get(getString(R.string.domain_name)+getString(R.string.user_details_by_mobile)+username)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userid = String.valueOf(response.getInt("id"));
                            User user = new User(userid,username,null);
                            Log.e("Writing User Details","true");
                            Paper.book().write("user",user);
                        } catch (JSONException e) {
                            Log.e("Writing User Exception",e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Writing Exception",anError.getMessage());
                    }
                });

    }



}
