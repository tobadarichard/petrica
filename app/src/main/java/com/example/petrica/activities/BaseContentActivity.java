package com.example.petrica.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Event;
import com.example.petrica.receivers.NetworkReceiver;

public abstract class BaseContentActivity extends AuthenticationActivity{
    // Base activity containing content
    protected NetworkReceiver networkReceiver;
    protected TextView connState; // TextView stating there is no connection
    protected boolean hasLoadingScreen;
    protected AlertDialog loadingScreen;
    protected boolean isListFinished = true;
    public static final String EXTRA_EVENT_TOSHOW = "com.example.petrica.EVENT";

    public class ItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EventAdapter eventAdapter = (EventAdapter) parent.getAdapter();
            Event event = (Event) eventAdapter.getItem(position);
            Intent i = new Intent(BaseContentActivity.this,EventDetailsActivity.class);
            i.putExtra(EXTRA_EVENT_TOSHOW,event);
            startActivity(i);
        }
    }

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
        model.getServerResponseLiveData().observe(this, new Observer<ServerResponse>() {
            @Override
            public void onChanged(ServerResponse serverResponse) {
                onServerResponse(serverResponse);
            }
        });
        networkReceiver = new NetworkReceiver(model.getHasConnection());
        IntentFilter intf = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,intf);

    }

    protected abstract void onServerResponse(ServerResponse serverResponse);

    protected void makeLoadingScreen(){
        if (!hasLoadingScreen){
            hasLoadingScreen = true;
            ProgressBar pb = new ProgressBar(this);
            pb.setIndeterminate(true);
            loadingScreen = (new AlertDialog.Builder(this)).setView(pb).create();
            loadingScreen.setCanceledOnTouchOutside(false);
            loadingScreen.setCancelable(false);
            loadingScreen.show();
        }
    }

    protected void removeLoadingScreen(){
        if (hasLoadingScreen){
            hasLoadingScreen = false;
            loadingScreen.setCancelable(true);
            loadingScreen.cancel();
        }
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
                if (!(this instanceof SettingsActivity)){
                    if (user == null){
                        askLogin();
                    }
                    else{
                        Intent intent = new Intent(BaseContentActivity.this,SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasLoadingScreen){
            loadingScreen.cancel();
        }
        // Unregister the network receiver
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        model.getServerResponseLiveData().setValue(new ServerResponse(ServerResponse.RESPONSE_TO_IGNORE,null,null,null));
    }
}
