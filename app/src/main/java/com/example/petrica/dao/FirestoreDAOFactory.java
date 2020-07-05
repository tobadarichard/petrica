package com.example.petrica.dao;

import androidx.lifecycle.MutableLiveData;

import com.example.petrica.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreDAOFactory {
    protected FirebaseFirestore db;
    protected static FirestoreDAOFactory factory;
    private FirestoreDAOFactory(){
        this.db = FirebaseFirestore.getInstance();
    }

    public static FirestoreDAOFactory getFactory(){
        if (factory == null){
            factory = new FirestoreDAOFactory();
        }
        return factory;
    }


    public FirestoreDAOEvent getDAOEvent(MutableLiveData<List<Event>> listComingEvents, MutableLiveData<List<Event>> listFollowedEvents, MutableLiveData<List<Event>> listSearchEvents) {
        return new FirestoreDAOEvent(db,listComingEvents,listFollowedEvents,listSearchEvents);
    }
}
