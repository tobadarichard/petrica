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

import androidx.lifecycle.Observer;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
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
    protected Switch onlyEventsFollowed;

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
        onlyEventsFollowed = findViewById(R.id.switch_only_follow);


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
        onlyEventsFollowed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && user == null){
                    onlyEventsFollowed.setChecked(false);
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

        model.getListSearchEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events == null){
                    labelResult.setText("");
                }
                else if (adapterEvent.isEmpty()){
                    int codeString = events.isEmpty() ? R.string.form_no_result : R.string.form_results;
                    labelResult.setText(codeString);
                }
                adapterEvent.addData(events);
            }
        });
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
        if(onlyEventsFollowed.isChecked() && user != null){
            uid = user.getUid();
        }
        model.searchEvents(min,max,name_org,name,theme,uid);
    }
}
