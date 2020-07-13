package com.example.petrica.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.petrica.R;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.exceptions.RatingException;
import com.example.petrica.model.Event;
import com.example.petrica.model.Rating;
import com.example.petrica.receivers.EventReceiver;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventDetailsActivity extends BaseContentActivity {
    // Activity showing details about a specific event
    protected ImageView image;
    protected TextView label_title;
    protected TextView label_description;
    protected TextView label_theme;
    protected TextView label_organiser;
    protected TextView label_date;
    protected TextView label_register;
    protected TextView label_rate;
    protected TextView label_avg_rate;
    protected LinearLayout star_group;
    protected Button[] buttons_star = new Button[5];
    protected Button button_comment;
    protected Button button_register;
    protected Event event;
    protected boolean isRegister;
    protected int rate;

    public class StarButtonListener implements View.OnClickListener{
        protected int which;
        public StarButtonListener(int which){
            super();
            this.which = which;
        }

        @Override
        public void onClick(View v) {
            boolean hasHappened = (new Date().getTime() - event.getDate().getTime()) > 0;
            if (user == null){
                askLogin();
            }
            else if (!hasHappened){
                (new AlertDialog.Builder(EventDetailsActivity.this)).setTitle(R.string.err)
                        .setMessage(R.string.detail_err_rate).create().show();
            }
            else if (which == (rate-1)){
                makeLoadingScreen();
                model.unwriteRating(user.getUid(),event.getId_event());
            }
            else{
                try {
                    Rating rating = new Rating(user.getUid(),user.getDisplayName(),(which+1));
                    makeLoadingScreen();
                    model.writeRating(rating,event.getId_event());
                } catch (RatingException ignored) {}
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_event_detail);

        // Finding views
        image = findViewById(R.id.image);
        label_title = findViewById(R.id.label_title);
        label_description = findViewById(R.id.label_description);
        label_theme = findViewById(R.id.label_theme);
        label_organiser = findViewById(R.id.label_organiser);
        label_register = findViewById(R.id.label_register);
        label_date = findViewById(R.id.label_date);
        label_rate = findViewById(R.id.label_rate);
        label_avg_rate = findViewById(R.id.label_avg_rate);
        star_group = findViewById(R.id.star_group);
        buttons_star[0] = findViewById(R.id.star1);
        buttons_star[1] = findViewById(R.id.star2);
        buttons_star[2] = findViewById(R.id.star3);
        buttons_star[3] = findViewById(R.id.star4);
        buttons_star[4] = findViewById(R.id.star5);
        button_comment = findViewById(R.id.button_comment);
        button_register = findViewById(R.id.button_register);

        // Adding listeners
        for (int i = 0; i <5; i++){
            buttons_star[i].setOnClickListener(new StarButtonListener(i));
        }

        button_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailsActivity.this,CommentsActivity.class);
                intent.putExtra(EXTRA_ID_EVENT,event.getId_event());
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasHappened = (new Date().getTime() - event.getDate().getTime()) > 0;
                if (user == null){
                    askLogin();
                }
                else if (hasHappened){
                    Toast.makeText(EventDetailsActivity.this,R.string.err_reg,Toast.LENGTH_LONG).show();
                }
                else {
                    makeLoadingScreen();
                    if (isRegister){
                        model.unwriteRegister(user.getUid(),event.getId_event());
                    }
                    else{
                        model.writeRegister(user.getUid(),event.getId_event());
                    }
                }
            }
        });
        // Getting the event to show
        if (savedInstanceState != null){
            event = savedInstanceState.getParcelable(EXTRA_EVENT_TOSHOW);
            isRegister = savedInstanceState.getBoolean(EXTRA_IS_REGISTERED);
            rate = savedInstanceState.getInt(EXTRA_RATE);
        }
        else{
            event = (Event) getIntent().getParcelableExtra(EXTRA_EVENT_TOSHOW);
            if (event == null) {
                // Finishing activity (nothing to show)
                finish();
            }
        }
        boolean eventToDownload = getIntent().getBooleanExtra(EXTRA_MUST_RETRIEVE_EVENT,false);
        if (eventToDownload && savedInstanceState == null){
            String id_event = getIntent().getStringExtra(EXTRA_ID_EVENT);
            model.loadEvent(id_event);
        }
        else{
            showEvent(savedInstanceState == null);
        }
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        int oldRating = rate;
        AlarmManager alarmManager;
        PendingIntent pendingIntent;
        switch (serverResponse.getResponseCode()){
            case ServerResponse.RESPONSE_DETAIL_EVENT_OK:
                List<Event> listE = serverResponse.getEventsList();
                if (listE.isEmpty()){
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setTitle(R.string.err).setMessage(R.string.err_404_details)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            }).create().show();
                }
                else{
                    event = listE.get(0);
                    showEvent(true);
                }
                break;
            case ServerResponse.RESPONSE_DETAIL_EVENT_ERROR:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(R.string.err).setMessage(R.string.err_retry)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        }).create().show();
                break;
            case ServerResponse.RESPONSE_INFO_USER_EVENT_OK:
                List<Rating> infoRating = serverResponse.getRatingList();
                if (infoRating.isEmpty()){
                    rate = -1;
                }
                else{
                    rate = infoRating.get(0).getNumber()-1;
                }
                isRegister = serverResponse.getEventsList() != null;
                refreshRegistered();
                refreshRating();
                break;
            case ServerResponse.RESPONSE_INFO_USER_EVENT_ERROR:
                isRegister = false;
                rate = -1;
                refreshRating();
                refreshRating();
                break;
            case ServerResponse.RESPONSE_WRITING_RATING_OK:
                Toast.makeText(this,R.string.detail_rate_successful,Toast.LENGTH_SHORT).show();
                rate = serverResponse.getRatingList().get(0).getNumber();
                event.updateAvgRating(oldRating,rate);
                refreshRating();
                break;
            case ServerResponse.RESPONSE_REGISTER_OK:
                Toast.makeText(this,R.string.detail_register_successful,Toast.LENGTH_SHORT).show();
                event.setNum_reg(event.getNum_reg()+1);
                isRegister = true;
                // Add alarm
                Intent i = new Intent(this,EventReceiver.class);
                i.setAction(EventReceiver.EVENT_NEAR);
                i.putExtra(EXTRA_ID_EVENT,event.getId_event());
                i.putExtra(EXTRA_NAME,event.getName());
                i.putExtra(EXTRA_DATE,event.getDate().getTime());
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                pendingIntent = PendingIntent.getBroadcast(this, 0, i,0);
                alarmManager.set(AlarmManager.RTC, event.getDate().getTime() -(1000*60*60),
                        pendingIntent);
                refreshRegistered();
                break;
            case ServerResponse.RESPONSE_DELETE_RATING_OK:
                Toast.makeText(this,R.string.detail_unrate_successful,Toast.LENGTH_SHORT).show();
                rate = -1;
                event.updateAvgRating(oldRating,rate);
                refreshRating();
                break;
            case ServerResponse.RESPONSE_UNREGISTER_OK:
                Toast.makeText(this,R.string.detail_unregister_successful,Toast.LENGTH_SHORT).show();
                event.setNum_reg(event.getNum_reg()-1);
                // Remove alarm
                isRegister = false;
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent ii = new Intent(this,EventReceiver.class);
                ii.setAction(EventReceiver.EVENT_NEAR);
                ii.putExtra(EXTRA_ID_EVENT,event.getId_event());
                ii.putExtra(EXTRA_NAME,event.getName());
                ii.putExtra(EXTRA_DATE,event.getDate().getTime());
                pendingIntent = PendingIntent.getBroadcast(this, 0, ii, 0);
                if (pendingIntent != null && alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }
                refreshRegistered();
                break;
            case ServerResponse.RESPONSE_WRITING_RATING_ERROR:
            case ServerResponse.RESPONSE_REGISTER_ERROR:
            case ServerResponse.RESPONSE_DELETE_RATING_ERROR:
            case ServerResponse.RESPONSE_UNREGISTER_ERROR:
                Toast.makeText(this,R.string.err_retry,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showEvent(boolean mustGetInfo){
        // Showing details
        Glide.with(image) // loading picture
                .load(FirebaseStorage.getInstance().getReference().child(event.getImage_path()))
                .into(image);
        if (user == null){
            isRegister = false;
            rate = -1;
            refreshRating();
            refreshRegistered();
        }
        else if (mustGetInfo){
            model.loadInfoUserOnEvent(user.getUid(),event.getId_event());
        }
        else{
            refreshRating();
            refreshRegistered();
        }
        label_title.setText(event.getName());
        label_description.setText(event.getDescription());
        label_theme.setText(getString(R.string.detail_theme,event.getTheme()));
        label_organiser.setText(getString(R.string.detail_organiser,event.getName_organiser()));

        // Date and remaining days
        String dateEvent = DateFormat.getInstance().format(event.getDate());
        long remainingTime = event.getDate().getTime() - (new Date()).getTime();
        Calendar dayEvent = Calendar.getInstance();
        dayEvent.setTime(event.getDate());
        int timeToMidnight = 1000*60*((60*dayEvent.get(Calendar.HOUR_OF_DAY)) + dayEvent.get(Calendar.MINUTE));
        if (remainingTime < 0){
            label_date.setText(getString(R.string.detail_remaining_already,dateEvent));
        }
        else if (remainingTime < timeToMidnight){
            label_date.setText(getString(R.string.detail_remaining_zero,dateEvent));
        }
        else {
            String remainingDays = String.valueOf(1 + ((remainingTime-timeToMidnight) / (1000*60*60*24)));
            label_date.setText(getString(R.string.detail_remaining_days,dateEvent,remainingDays));
        }
    }

    public void refreshRating(){
        String data = event.getNum_rate() == 0 ? getString(R.string.detail_avg_rate_not_available)
                : getString(R.string.detail_avg_rate,event.getAvg_rate());
        label_avg_rate.setText(data);
        // Changing state of all buttons before the one selected and the one selected
        for (int i = 0; i < 5; i++){
            if (i <= (rate-1)){
                buttons_star[i].setBackgroundResource(android.R.drawable.btn_star_big_on);
            }
            else {
                buttons_star[i].setBackgroundResource(android.R.drawable.btn_star);
            }
        }
    }

    public void refreshRegistered(){
        String data = event.getNum_reg() == 0 ? getString(R.string.detail_zero_registered)
                : getString(R.string.detail_some_registered,event.getNum_reg());
        label_register.setText(data);
        data = isRegister ? getString(R.string.detail_unregister)
                : getString(R.string.detail_register);
        button_register.setText(data);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_EVENT_TOSHOW,event);
        outState.putBoolean(EXTRA_IS_REGISTERED,isRegister);
        outState.putInt(EXTRA_RATE,rate);
    }

    @Override
    public void refresh() {
        makeLoadingScreen();
        model.loadEvent(event.getId_event());
    }

    @Override
    public void onUserDisconnect() {
        isRegister = false;
        rate = -1;
        refreshRating();
        refreshRegistered();
    }

    @Override
    public void onUserConnect() {
        String id_event;
        if (event != null){
            id_event = event.getId_event();
        }
        else {
            id_event = getIntent().getStringExtra(EXTRA_ID_EVENT);
        }
        if (id_event == null){
            finish();
        }
        model.loadInfoUserOnEvent(user.getUid(),id_event);
    }
}
