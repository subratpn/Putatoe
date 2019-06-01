package com.putatoe.app.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.putatoe.app.R;
import com.putatoe.app.activity.HomeActivity;
import com.putatoe.app.activity.MobileLoginActivity;

import org.w3c.dom.Text;

import io.paperdb.Paper;
import needle.Needle;
import needle.UiRelatedTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private CardView customerCareView;
    private static int CALL_REQUEST_CODE = 200;
    private static String CUSTOMER_CARE_NUMBER="9889540854";
    private CardView logoutView;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        customerCareView = v.findViewById(R.id.customer_care_view);
        customerCareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustomerCare();
            }
        });
        logoutView = (CardView) v.findViewById(R.id.logout_view);
        logoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Needle.onBackgroundThread().execute(new UiRelatedTask<Boolean>() {
                    @Override
                    protected Boolean doWork() {
                        Paper.init(getActivity());
                        Paper.book().delete("token");
                        return true;
                    }

                    @Override
                    protected void thenDoUiRelatedWork(final Boolean result) {
                        startActivity(new Intent(getActivity(), MobileLoginActivity.class));
                        getActivity().finishAffinity();
                    }
                });
            }
        });

        return v;
    }

    private void callCustomerCare() {


            Log.e("ProfileFragment","Called Customer Care");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ) {

                Log.e("ProfileFragment","Flow 1");
                requestPermissions(
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_REQUEST_CODE);
            }
            else {

                Log.e("ProfileFragment","Flow 2");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+"9889540854"));
                startActivity(callIntent);

            }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case 200: {

                Log.e("ProfileFragment","Inside Callback");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED ) {

                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+CUSTOMER_CARE_NUMBER));
                        startActivity(callIntent);

                    }

                }
            }
        }
    }

}
