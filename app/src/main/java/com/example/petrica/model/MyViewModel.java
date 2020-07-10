package com.example.petrica.model;

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

    public void init(){
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance();
            user.setValue(firebaseAuthInstance.getCurrentUser());
        }
        DAOEvent = FirestoreDAOFactory.getFactory().getDAOEvent(serverResponseLiveData);
        DAORating = FirestoreDAOFactory.getFactory().getDAORating(serverResponseLiveData);
        DAOComment = FirestoreDAOFactory.getFactory().getDAOComment(serverResponseLiveData);
    }

    public FirebaseAuth getFirebaseAuthInstance() {
        return firebaseAuthInstance;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        user.setValue(firebaseAuthInstance.getCurrentUser());
        return user;
    }

    public MutableLiveData<Boolean> getHasConnection() {
        return hasConnection;
    }

    public void loadComingEvents() {
        DAOEvent.getComingEvents(5);
    }

    public void loadRegisteredEvents(FirebaseUser u) {
        if (u != null){
            DAOEvent.getRegisteredEvents(5,u.getUid());
        }
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
}
