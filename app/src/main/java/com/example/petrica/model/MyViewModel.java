package com.example.petrica.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.petrica.dao.FirestoreDAOEvent;
import com.example.petrica.dao.FirestoreDAOFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MyViewModel extends ViewModel{
    // Model containing data that should outlive activity
    private FirebaseAuth firebaseAuthInstance;
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasConnection = new MutableLiveData<>();
    private MutableLiveData<List<Event>> listComingEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> listFollowedEvents = new MutableLiveData<>();
    private MutableLiveData<List<Event>> listSearchEvents = new MutableLiveData<>();
    private FirestoreDAOEvent DAOEvent = FirestoreDAOFactory.getFactory().getDAOEvent(listComingEvents,listFollowedEvents,listSearchEvents);

    public void init(){
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance();
            user.setValue(firebaseAuthInstance.getCurrentUser());
        }
    }

    public FirebaseAuth getFirebaseAuthInstance() {
        return firebaseAuthInstance;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        user.setValue(firebaseAuthInstance.getCurrentUser());
        return user;
    }

    public MutableLiveData<List<Event>> getListComingEvents() {
        return listComingEvents;
    }

    public MutableLiveData<List<Event>> getListFollowedEvents() {
        return listFollowedEvents;
    }

    public MutableLiveData<List<Event>> getListSearchEvents() {
        return listSearchEvents;
    }

    public MutableLiveData<Boolean> getHasConnection() {
        return hasConnection;
    }

    public void loadComingEvents() {
        DAOEvent.getComingEvents(5);
    }

    public void loadFollowedEvents(FirebaseUser u) {
        if (u != null){
            DAOEvent.getFollowedEvents(5,u.getUid());
        }
    }
}
