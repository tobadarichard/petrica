package com.example.petrica.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchActivity extends BaseContentActivity{
    protected SearchView search;
    protected EditText inputNameOrga;
    protected Spinner listTheme;
    protected Switch hasDateMin;
    protected CalendarView calendarDateMin;
    protected Switch hasDateMax;
    protected CalendarView calendarDateMax;
    protected Button buttonSearch;
    protected EventAdapter adapterEvent;
    protected ListView listResult;
    protected TextView labelResult;
    protected Switch onlyEventsRegistered;
    protected Date dateMin;
    protected Date dateMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_search);

        // Getting views
        inputNameOrga = findViewById(R.id.input_orga_name);
        listTheme = findViewById(R.id.list_theme);
        hasDateMin = findViewById(R.id.switch_date_min);
        calendarDateMin = findViewById(R.id.date_min);
        hasDateMax = findViewById(R.id.switch_date_max);
        calendarDateMax = findViewById(R.id.date_max);
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

        dateMin = new Date();
        hasDateMin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    calendarDateMin.setVisibility(View.VISIBLE);
                }
                else{
                    calendarDateMin.setVisibility(View.GONE);
                }
            }
        });
        calendarDateMin.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.clear();
                c.set(year,month,dayOfMonth);
                dateMin = c.getTime();
            }
        });

        dateMax = new Date();
        hasDateMax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    calendarDateMax.setVisibility(View.VISIBLE);
                }
                else{
                    calendarDateMax.setVisibility(View.GONE);
                }
            }
        });
        calendarDateMax.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.clear();
                c.set(year,month,dayOfMonth);
                dateMax = c.getTime();
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.theme_research_values,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listTheme.setAdapter(adapter);

        adapterEvent = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        listResult.setAdapter(adapterEvent);
        listResult.setOnItemClickListener(new ItemClickEventListener());

        if (savedInstanceState != null){
            model.getServerResponseLiveData().setValue(
                    new ServerResponse(ServerResponse.RESPONSE_SEARCHED_EVENT_FIRST,model.getSavedSearch(),null,null));
        }
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        List<Event> events = serverResponse.getEventsList();
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
    }

    @Override
    public void onRefresh() {
        searchEvents();
    }

    @Override
    public void onUserDisconnect() {
        onlyEventsRegistered.setChecked(false);
    }

    @Override
    public void onUserConnect() {
        // Nothing to do
    }

    private void searchEvents() {
        // Retrieving parameters
        Date min = hasDateMin.isChecked() ? dateMin : null;
        Date max = hasDateMax.isChecked() ? dateMax : null;
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
