package com.example.petrica.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.petrica.R;
import com.example.petrica.adapters.EventAdapter;
import com.example.petrica.dao.ServerResponse;
import com.example.petrica.model.Event;

import java.util.ArrayList;

public class DeleteEventActivity extends BaseContentActivity {
    protected EventAdapter adapter;
    protected String id_event_to_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_delete_event);
        ListView listView = findViewById(R.id.list_event_user);
        adapter = new EventAdapter(new ArrayList<Event>(),getLayoutInflater());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Event e = (Event) adapter.getItem(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(DeleteEventActivity.this);
                adb.setTitle(R.string.delete_event_title).setMessage(R.string.delete_event_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE){
                                    makeLoadingScreen();
                                    id_event_to_delete = e.getId_event();
                                    model.removeEvent(e.getId_event(),user.getUid());
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE){
                            dialog.cancel();
                        }
                    }
                }).create().show();
            }
        });
        if (savedInstanceState != null){
            adapter.addData(model.getSavedSearch());
        }
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        switch (serverResponse.getResponseCode()){
            case ServerResponse.RESPONSE_DETAIL_EVENT_OK:
                adapter.clear(true);
                adapter.addData(serverResponse.getEventsList());
                break;
            case ServerResponse.RESPONSE_DETAIL_EVENT_ERROR:
                Toast.makeText(this,R.string.err_retry,Toast.LENGTH_LONG).show();
                finish();
                break;
            case ServerResponse.RESPONSE_DELETE_EVENT_OK:
                Toast.makeText(this,R.string.delete_event_successful,Toast.LENGTH_LONG).show();
                adapter.deleteItem(id_event_to_delete,true);
                break;
            case ServerResponse.RESPONSE_DELETE_EVENT_ERROR:
                Toast.makeText(this,R.string.err_retry,Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onRefresh() {
        model.loadEventsCreated();
    }

    @Override
    public void onUserDisconnect() {
        // Finish
        finish();
    }

    @Override
    public void onUserConnect() {
        model.loadEventsCreated();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        model.setSavedSearch(adapter.getData());
    }
}
