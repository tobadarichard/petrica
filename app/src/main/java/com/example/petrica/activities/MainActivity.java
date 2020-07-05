package com.example.petrica.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
import com.example.petrica.model.Event;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseContentActivity {
    // This is the main page of the app
    protected EventAdapter adapterComing;
    protected EventAdapter adapterFollowed;
    protected TextView hello;
    protected boolean hasEmptyMessageComing = false;
    protected boolean hasEmptyMessageFollowed = false;
    protected LinearLayout linear;
    public static final int EMPTY_POSITION_COMING = 5;
    public static final int EMPTY_POSITION_FOLLOWED = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting view
        connState = findViewById(R.id.conn_state);
        hello = findViewById(R.id.hello_label);
        linear = findViewById(R.id.linear);

        // Setting the toolbar
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        // Setting observer for adapter and coming events
        adapterComing = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        model.getListComingEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (!events.isEmpty()){
                    // remove the empty message
                    removeEmptyMessage(EMPTY_POSITION_COMING);
                    adapterComing.addData(events);
                }
                else if (adapterComing.isEmpty()){
                    // Add the empty message
                    addEmptyMessage(EMPTY_POSITION_COMING);
                }
            }
        });
        // Setting observer for adapter and followed events
        adapterFollowed = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        model.getListFollowedEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (!events.isEmpty()){
                    // remove the empty message
                    removeEmptyMessage(EMPTY_POSITION_FOLLOWED);
                    adapterFollowed.addData(events);
                }
                else if (adapterFollowed.isEmpty()){
                    // Add the empty message
                    addEmptyMessage(EMPTY_POSITION_FOLLOWED);
                }
            }
        });


        // Showing welcome message if user is logged
        model.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    hello.setText(getResources().getString(R.string.hello_label,firebaseUser.getDisplayName()));
                    hello.setVisibility(View.VISIBLE);
                    // Activate followed events
                    if (adapterFollowed.isEmpty()){
                        model.loadFollowedEvents(firebaseUser);
                    }
                }
                else{
                    hello.setVisibility(View.INVISIBLE);
                    addEmptyMessage(EMPTY_POSITION_FOLLOWED,getString(R.string.need_sign_follow_events));
                }
            }
        });
        if (savedInstanceState == null){
            model.loadComingEvents();
        }
        ListView lw1 = findViewById(R.id.coming_events);
        lw1.setAdapter(adapterComing);
        ListView lw2 = findViewById(R.id.follow_events);
        lw2.setAdapter(adapterFollowed);
    }

    private void removeEmptyMessage(int emptyPosition) {
        switch (emptyPosition){
            case EMPTY_POSITION_COMING:
                if (!hasEmptyMessageComing){
                    return;
                }
                hasEmptyMessageComing = false;
                break;
            case EMPTY_POSITION_FOLLOWED:
                if (!hasEmptyMessageFollowed){
                    return;
                }
                hasEmptyMessageFollowed = false;
                break;
        }
        linear.removeViewAt(emptyPosition);
    }

    private void addEmptyMessage(int emptyPosition) {
        addEmptyMessage(emptyPosition,getString(R.string.no_result));
    }

    private void addEmptyMessage(int emptyPosition,String message) {
        switch (emptyPosition){
            case EMPTY_POSITION_COMING:
                if (hasEmptyMessageComing){
                    return;
                }
                hasEmptyMessageComing = true;
                break;
            case EMPTY_POSITION_FOLLOWED:
                if (hasEmptyMessageFollowed){
                    return;
                }
                hasEmptyMessageFollowed = true;
                break;
        }
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setGravity(Gravity.CENTER);
        linear.addView(tv,emptyPosition);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && user !=null){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}