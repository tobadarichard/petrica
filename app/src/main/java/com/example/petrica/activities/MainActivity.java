package com.example.petrica.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Event;
import com.example.petrica.views.NonScrollListView;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseContentActivity {
    // This is the main page of the app
    protected EventAdapter adapterComing;
    protected EventAdapter adapterRegistered;
    protected TextView hello;
    protected boolean hasEmptyMessageComing = false;
    protected boolean hasEmptyMessageRegistered = false;
    protected LinearLayout linear;
    public static final int EMPTY_POSITION_COMING = 5;
    public static final int EMPTY_POSITION_FOLLOWED = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_main);
        // Getting view
        hello = findViewById(R.id.hello_label);
        linear = findViewById(R.id.linear);

        // Setting observer for adapter and coming events
        adapterComing = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        NonScrollListView lw1 = findViewById(R.id.coming_events);
        lw1.setAdapter(adapterComing);
        lw1.setOnItemClickListener(new ItemClickListener());
        // Setting observer for adapter and registered events
        adapterRegistered = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        NonScrollListView lw2 = findViewById(R.id.registered_events);
        lw2.setAdapter(adapterRegistered);
        lw2.setOnItemClickListener(new ItemClickListener());

        // Showing welcome message if user is logged
        model.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser != null){
                    hello.setText(getResources().getString(R.string.hello_label,firebaseUser.getDisplayName()));
                    hello.setVisibility(View.VISIBLE);
                    // Activate registered events
                    model.loadRegisteredEvents(firebaseUser);
                }
                else{
                    hello.setVisibility(View.INVISIBLE);
                    addEmptyMessage(EMPTY_POSITION_FOLLOWED,getString(R.string.need_sign_registered_events));
                }
            }
        });
        model.loadComingEvents();
    }

    @Override
    protected void onServerResponse(ServerResponse serverResponse) {
        List<Event> events = serverResponse.getEventsList();
        switch (serverResponse.getResponseCode()){
            case ServerResponse.RESPONSE_COMING_EVENT:
                adapterComing.addData(events);
                if (events != null && !events.isEmpty()){
                    // remove the empty message
                    removeEmptyMessage(EMPTY_POSITION_COMING);
                }
                else if (adapterComing.isEmpty()){
                    // Add the empty message
                    addEmptyMessage(EMPTY_POSITION_COMING);
                }
                break;
            case ServerResponse.RESPONSE_REGISTERED_EVENT:
                adapterRegistered.addData(events);
                if (events != null && !events.isEmpty()){
                    // remove the empty message
                    removeEmptyMessage(EMPTY_POSITION_FOLLOWED);

                }
                else if (adapterRegistered.isEmpty()){
                    // Add the empty message
                    addEmptyMessage(EMPTY_POSITION_FOLLOWED);
                }
                break;
        }
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
                if (!hasEmptyMessageRegistered){
                    return;
                }
                hasEmptyMessageRegistered = false;
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
                if (hasEmptyMessageRegistered){
                    return;
                }
                hasEmptyMessageRegistered = true;
                break;
        }
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setGravity(Gravity.CENTER);
        linear.addView(tv,emptyPosition);
    }
}