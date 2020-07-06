package com.example.petrica.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.receivers.NetworkReceiver;

public abstract class BaseContentActivity extends AuthenticationActivity{
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
                    connState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        networkReceiver = new NetworkReceiver(model.getHasConnection());
        IntentFilter intf = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,intf);

    }

    public void setupHeader(int layout){
        setContentView(layout);
        // Setting the toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        // Getting view
        connState = findViewById(R.id.conn_state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        // TODO : Menu
        switch(item.getItemId()){
            case R.id.toolbar_event:
                if (!(this instanceof SearchActivity)){
                    Intent intent = new Intent(BaseContentActivity.this,SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                break;
            case R.id.toolbar_home:
                if (!(this instanceof MainActivity)){
                    Intent intent = new Intent(BaseContentActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                }
                break;
            case R.id.toolbar_setting:
                if (user == null){
                    askLogin();
                }
                else{
                    // TODO : Starting settings activity
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }
}
