package com.example.petrica.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.MutableLiveData;

public class NetworkReceiver extends BroadcastReceiver {
    private final MutableLiveData<Boolean> hasConnection;
    public NetworkReceiver(MutableLiveData<Boolean> hasConnection) {
        super();
        this.hasConnection = hasConnection;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        if (networkInfo != null){
            hasConnection.setValue(true);
        }
        else{
            hasConnection.setValue(false);
        }
    }
}
