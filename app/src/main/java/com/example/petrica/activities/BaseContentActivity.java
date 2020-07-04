package com.example.petrica.activities;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.receivers.NetworkReceiver;

public class BaseContentActivity extends AuthenticationActivity{
    // Base activity containing content
    protected NetworkReceiver networkReceiver;
    protected TextView connState; // TextView stating there is no connection
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the network receiver
        model.getHasConnection().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean conn) {
                if (conn){
                    connState.setVisibility(View.INVISIBLE);
                }
                else{
                    connState.setVisibility(View.VISIBLE);
                }
            }
        });
        networkReceiver = new NetworkReceiver(model.getHasConnection());
        IntentFilter intf = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,intf);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
