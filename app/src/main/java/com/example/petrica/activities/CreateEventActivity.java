package com.example.petrica.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petrica.R;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Event;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends BaseContentActivity {
    protected Button button_manage_event;
    protected EditText input_name;
    protected Spinner list_theme;
    protected CalendarView calendarView;
    protected Date date_event;
    protected EditText input_description;
    protected Button button_image;
    protected ImageView image;
    protected Uri image_uri;
    protected Button button_create;
    protected boolean hasChooseImage = false;
    protected String organiser_name;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_create_event);

        if (savedInstanceState != null){
            organiser_name = savedInstanceState.getString(EXTRA_ORGANISER_NAME);
        }
        else{
            organiser_name = getIntent().getStringExtra(EXTRA_ORGANISER_NAME);
        }
        if (organiser_name == null){
            finish();
        }
        
        // Finding views
        button_manage_event = findViewById(R.id.button_manage_event);
        input_name = findViewById(R.id.input_name);
        list_theme = findViewById(R.id.list_theme_create);
        calendarView = findViewById(R.id.date);
        date_event = new Date();
        input_description = findViewById(R.id.input_description);
        button_image = findViewById(R.id.button_image);
        image = findViewById(R.id.view_image);
        button_create = findViewById(R.id.button_create);

        // Adding listeners
        button_manage_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this,DeleteEventActivity.class);
                startActivity(intent);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.clear();
                c.set(year,month,dayOfMonth,12,0);
                date_event = c.getTime();
            }
        });

        button_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_image)), RESULT_CHOOSE_IMAGE);
            }
        });

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getHasConnection().getValue())
                    createEvent();
            }
        });

        // Set adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.theme_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_theme.setAdapter(adapter);

    }

    public void createEvent() {
        // Retrieving data
        String name = input_name.getText().toString();
        if (name.length() < 6){
            Toast.makeText(this,R.string.err_name_to_short,Toast.LENGTH_SHORT).show();
            return;
        }
        String theme = list_theme.getSelectedItem().toString();
        String description = input_description.getText().toString();
        if (description.length() < 30){
            Toast.makeText(this,R.string.err_description_to_short,Toast.LENGTH_SHORT).show();
            return;
        }
        if (!hasChooseImage){
            Toast.makeText(this,R.string.err_no_image,Toast.LENGTH_SHORT).show();
            return;
        }
        Event e = new Event(date_event,description,user.getUid(),organiser_name,null,name,theme,null,
                0,0,0);
        model.addEvent(e,image_uri);
        makeLoadingScreen();
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        switch (serverResponse.getResponseCode()){
            case ServerResponse.RESPONSE_WRITING_EVENT_OK:
                Toast.makeText(this,R.string.event_write_successful,Toast.LENGTH_SHORT).show();
                input_description.setText("");
                input_name.setText("");
                break;
            case ServerResponse.RESPONSE_WRITING_EVENT_ERROR:
                Toast.makeText(this,R.string.err_retry,Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void refresh() {
        // Nothing to do
    }

    @Override
    public void onUserDisconnect() {
        // Finish activity
        finish();
    }

    @Override
    public void onUserConnect() {
        // Nothing to do
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // Show picture
            hasChooseImage = true;
            image_uri = data.getData();
            image.setImageURI(image_uri);
            image.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_ORGANISER_NAME,organiser_name);
    }
}
