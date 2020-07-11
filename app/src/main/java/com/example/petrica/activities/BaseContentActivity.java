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
import com.example.petrica.receivers.EventReceiver;
import com.example.petrica.receivers.NetworkReceiver;


public abstract class BaseContentActivity extends AuthenticationActivity{
    // Base activity containing content
    protected boolean hasLoadingScreen;
    protected AlertDialog loadingScreen;
    protected boolean isListFinished = false;
    protected NetworkReceiver networkReceiver;
    protected EventReceiver eventReceiver;
    protected TextView connState; // TextView stating there is no connection

    public static final String EXTRA_EVENT_TOSHOW = "com.example.petrica.EVENT";
    public static final String EXTRA_ID_EVENT = "com.example.petrica.ID_EVENT";
    public static final String EXTRA_MUST_RETRIEVE_EVENT = "com.example.petrica.MUST_RETRIEVE_EVENT";
    public static final String EXTRA_IS_REGISTERED = "com.example.petrica.IS_REGISTERED";
    public static final String EXTRA_RATE = "com.example.petrica.RATE";
    public static final String EXTRA_NAME = "com.example.petrica.NAME";
    public static final String EXTRA_DATE = "com.example.petrica.DATE";
    public static final String EXTRA_IS_LIST_FINISHED = "com.example.petrica.IS_LIST_FINISHED";

    public class ItemClickEventListener implements ListView.OnItemClickListener{
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
        // Observe server response
        model.getServerResponseLiveData().observe(this, new Observer<ServerResponse>() {
            @Override
            public void onChanged(ServerResponse serverResponse) {
                removeLoadingScreen();
                if (serverResponse.getResponseCode() != ServerResponse.RESPONSE_TO_IGNORE){
                    onServerResponse(serverResponse);
                }
            }
        });
        networkReceiver = new NetworkReceiver(model.getHasConnection());
        IntentFilter intf = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver,intf);

        eventReceiver = new EventReceiver();
        IntentFilter intf2 = new IntentFilter();
        intf2.addAction(EventReceiver.EVENT_NEAR);
        intf2.addAction(EventReceiver.EVENT_FINISHED);
        registerReceiver(eventReceiver,intf2);
    }

    public void makeLoadingScreen(){
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

    public void removeLoadingScreen(){
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
        // Observe network state
        model.getHasConnection().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean conn) {
                if (conn && connState.getVisibility() == View.VISIBLE){
                    connState.setVisibility(View.INVISIBLE);
                    onRefresh();
                }
                else if (!conn && connState.getVisibility() == View.INVISIBLE){
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
                    finish();
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
            case R.id.toolbar_add:
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
        unregisterReceiver(eventReceiver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Set an empty response to prevent observer from getting false news when restart
        model.getServerResponseLiveData().setValue(new ServerResponse(ServerResponse.RESPONSE_TO_IGNORE,null,null,null));
    }

    // What to do when server respond or when refresh data is needed ?
    public abstract void onServerResponse(ServerResponse serverResponse);
    public abstract void onRefresh();
}
