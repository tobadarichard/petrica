package com.example.petrica.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.petrica.dao.FirestoreDAOComment;
import com.example.petrica.dao.FirestoreDAOEvent;
import com.example.petrica.dao.FirestoreDAOFactory;
import com.example.petrica.dao.FirestoreDAORating;
import com.example.petrica.dao.ServerResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

public class MyViewModel extends ViewModel{
    // Model containing data that should outlive activity
    private FirebaseAuth firebaseAuthInstance;
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasConnection = new MutableLiveData<>();
    private MutableLiveData<ServerResponse> serverResponseLiveData = new MutableLiveData<>();
    private FirestoreDAOEvent DAOEvent;
    private FirestoreDAORating DAORating;
    private FirestoreDAOComment DAOComment;
    private List<Event> savedSearch;
    private List<Event> savedRegistered;
    private List<Event> savedComing;
    private List<Comment> savedComment;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onCleared() {
        super.onCleared();
        firebaseAuthInstance.removeAuthStateListener(listener);
    }

    public void init(){
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance();
            listener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user.setValue(firebaseAuth.getCurrentUser());
                }
            };
            firebaseAuthInstance.addAuthStateListener(listener);
        }
        DAOEvent = FirestoreDAOFactory.getFactory().getDAOEvent(serverResponseLiveData);
        DAORating = FirestoreDAOFactory.getFactory().getDAORating(serverResponseLiveData);
        DAOComment = FirestoreDAOFactory.getFactory().getDAOComment(serverResponseLiveData);
    }

    public FirebaseAuth getFirebaseAuthInstance() {
        return firebaseAuthInstance;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }

    public MutableLiveData<Boolean> getHasConnection() {
        return hasConnection;
    }

    public void loadComingEvents() {
        DAOEvent.getComingEvents(5);
    }

    public void loadRegisteredEvents(String uid) {
        DAOEvent.getRegisteredEvents(5,uid);
    }

    public MutableLiveData<ServerResponse> getServerResponseLiveData() {
        return serverResponseLiveData;
    }

    public void searchEvents(Date min, Date max, String nameOrg, String name, String theme, String uid) {
        DAOEvent.getFilteredEvents(ServerResponse.RESPONSE_SEARCHED_EVENT_FIRST,ServerResponse.RESPONSE_SEARCHED_EVENT_FIRST,10,min,max,nameOrg,name,theme,uid);

    }

    public void getInfoUserOnEvent(String uid,String id_event){
        DAOEvent.getInfoUserOnEvent(uid,id_event);
    }

    public void writeRating(Rating rating,String id_event){
        DAORating.rate(rating,id_event);
    }

    public void unwriteRating(String uid,String id_event){
        DAORating.unrate(uid,id_event);
    }

    public void writeRegister(String uid, String id_event) {
        DAOEvent.register(uid,id_event);
    }

    public void unwriteRegister(String uid, String id_event) {
        DAOEvent.unregister(uid,id_event);
    }

    public void writeComment(Comment c, String id_event){
        DAOComment.writeComment(c,id_event);
    }

    public void unwriteComment(String id_doc_comment, String id_event){
        DAOComment.unwriteComment(id_doc_comment,id_event);
    }

    public void loadComments(String id_event){
        DAOComment.getComments(id_event,null,20);
    }

    public void loadUserComments(String id_event,String id_user){
        DAOComment.getComments(id_event,id_user,-1);
    }

    public List<Event> getSavedSearch() {
        return savedSearch;
    }

    public void setSavedSearch(List<Event> savedSearch) {
        this.savedSearch = savedSearch;
    }

    public void loadEvent(String id_event){
        DAOEvent.getEvent(id_event);
    }

    public List<Event> getSavedRegistered() {
        return savedRegistered;
    }

    public void setSavedRegistered(List<Event> savedRegistered) {
        this.savedRegistered = savedRegistered;
    }

    public List<Event> getSavedComing() {
        return savedComing;
    }

    public void setSavedComing(List<Event> savedComing) {
        this.savedComing = savedComing;
    }

    public List<Comment> getSavedComment() {
        return savedComment;
    }

    public void setSavedComment(List<Comment> savedComment) {
        this.savedComment = savedComment;
    }
}
