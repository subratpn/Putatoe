package com.putatoe.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putatoe.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {


    private TextView walletBalanceView;
    private LinearLayout walletRootView;
    private Button shareButton;

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);
        walletBalanceView = (TextView) v.findViewById(R.id.wallet_balance_view);
        walletRootView = (LinearLayout) v.findViewById(R.id.wallet_root_view);
        shareButton = (Button) v.findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, getString(R.string.referral_share_message) );
                startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });

        walletBalanceView.setText(getString(R.string.Rs)+"50");
        return v;
    }

}
