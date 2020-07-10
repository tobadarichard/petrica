package com.example.petrica.dao;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;

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


    public FirestoreDAOEvent getDAOEvent(MutableLiveData<ServerResponse> serverResponse) {
        return new FirestoreDAOEvent(db,serverResponse);
    }

    public FirestoreDAORating getDAORating(MutableLiveData<ServerResponse> serverResponse){
        return new FirestoreDAORating(db,serverResponse);
    }

    public FirestoreDAOComment getDAOComment(MutableLiveData<ServerResponse> serverResponse) {
        return new FirestoreDAOComment(db,serverResponse);
    }
}
