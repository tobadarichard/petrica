package com.example.petrica.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Event;
import com.example.petrica.views.NonScrollListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends BaseContentActivity{
    protected SearchView search;
    protected EditText inputNameOrga;
    protected Spinner listTheme;
    protected Switch hasDateMin;
    protected CalendarView dateMin;
    protected Switch hasDateMax;
    protected CalendarView dateMax;
    protected Button buttonSearch;
    protected EventAdapter adapterEvent;
    protected NonScrollListView listResult;
    protected TextView labelResult;
    protected Switch onlyEventsRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_search);

        // Getting views
        inputNameOrga = findViewById(R.id.input_orga_name);
        listTheme = findViewById(R.id.list_theme);
        hasDateMin = findViewById(R.id.switch_date_min);
        dateMin = findViewById(R.id.date_min);
        hasDateMax = findViewById(R.id.switch_date_max);
        dateMax = findViewById(R.id.date_max);
        search = findViewById(R.id.search_view);
        buttonSearch = findViewById(R.id.button_search);
        listResult = findViewById(R.id.list_results);
        labelResult = findViewById(R.id.label_num_results);
        onlyEventsRegistered = findViewById(R.id.switch_only_registered);


        // Adding listeners
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEvents();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEvents();
            }
        });
        // TODO : ANIMATION ?
        hasDateMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dateMin.setVisibility(View.VISIBLE);
                }
                else{
                    dateMin.setVisibility(View.GONE);
                }
            }
        });

        hasDateMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dateMax.setVisibility(View.VISIBLE);
                }
                else{
                    dateMax.setVisibility(View.GONE);
                }
            }
        });
        onlyEventsRegistered.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && user == null){
                    onlyEventsRegistered.setChecked(false);
                    askLogin();
                }
            }
        });

        // Set adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.theme_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listTheme.setAdapter(adapter);

        adapterEvent = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        listResult.setAdapter(adapterEvent);
        listResult.setOnItemClickListener(new ItemClickListener());

        if (savedInstanceState != null){
            model.getServerResponseLiveData().setValue(
                    new ServerResponse(ServerResponse.RESPONSE_SEARCHED_EVENT_FIRST,model.getSavedSearch(),null,null));
        }
    }

    @Override
    protected void onServerResponse(ServerResponse serverResponse) {
        List<Event> events = serverResponse.getEventsList();
        if (serverResponse.getResponseCode() == ServerResponse.RESPONSE_TO_IGNORE){
            removeLoadingScreen();
            return;
        }
        switch (serverResponse.getResponseCode()) {
            case ServerResponse.RESPONSE_SEARCHED_EVENT_FIRST:
                isListFinished = false;
                if (events == null) {
                    labelResult.setText(R.string.err_retry);
                } else {
                    int codeString = events.isEmpty() ? R.string.form_no_result : R.string.form_results;
                    labelResult.setText(codeString);
                    adapterEvent.clear(false);
                    adapterEvent.addData(events);
                }
                break;
            case ServerResponse.RESPONSE_SEARCHED_EVENT_NEXT:
                if (events == null) {
                    Toast.makeText(this, R.string.err_retry, Toast.LENGTH_LONG).show();
                } else {
                    adapterEvent.addData(events);
                }
                break;
            case ServerResponse.RESPONSE_SEARCHED_EVENT_END:
                if (events == null) {
                    Toast.makeText(this, R.string.err_retry, Toast.LENGTH_LONG).show();
                } else {
                    isListFinished = true;
                }
                break;
        }
        removeLoadingScreen();
    }

    private void searchEvents() {
        // Retrieving parameters
        Date min = hasDateMin.isChecked() ? new Date(dateMin.getDate()) : null;
        Date max = hasDateMax.isChecked() ? new Date(dateMax.getDate()) : null;
        String name_org = inputNameOrga.getText().toString();
        name_org = name_org.equals("") ? null : name_org;
        String name = search.getQuery().toString();
        name = name.equals("") ? null : name;
        String theme = listTheme.getSelectedItem().toString();
        theme = theme.equals("Any") ? null : theme;
        String uid = null;
        if(onlyEventsRegistered.isChecked() && user != null){
            uid = user.getUid();
        }
        makeLoadingScreen();
        model.searchEvents(min,max,name_org,name,theme,uid);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        model.setSavedSearch(adapterEvent.getData());
    }
}
