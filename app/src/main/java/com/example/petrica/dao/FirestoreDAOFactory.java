package com.example.petrica.dao;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirestoreDAOFactory {
    protected final FirebaseFirestore db;
    protected final FirebaseStorage storage;
    protected static FirestoreDAOFactory factory;
    private FirestoreDAOFactory(){
        this.storage = FirebaseStorage.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public static FirestoreDAOFactory getFactory(){
        if (factory == null){
            factory = new FirestoreDAOFactory();
        }
        return factory;
    }


    public FirestoreDAOEvent getDAOEvent(MutableLiveData<ServerResponse> serverResponse) {
        return new FirestoreDAOEvent(db,storage,serverResponse);
    }

    public FirestoreDAORating getDAORating(MutableLiveData<ServerResponse> serverResponse){
        return new FirestoreDAORating(db,serverResponse);
    }

    public FirestoreDAOComment getDAOComment(MutableLiveData<ServerResponse> serverResponse) {
        return new FirestoreDAOComment(db,serverResponse);
    }
}
