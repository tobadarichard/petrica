package com.example.petrica.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.petrica.exceptions.RatingException;
import com.example.petrica.model.Event;
import com.example.petrica.model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDAOEvent {
    protected FirebaseFirestore db;
    protected MutableLiveData<ServerResponse> serverResponse;
    protected DocumentSnapshot lastVisible;
    protected Query lastQuery;
    protected String lastName;

    public class FilteredEventsResultListener implements OnCompleteListener<QuerySnapshot>{
        private int responseCodeOk;
        private int responseCodeError;
        private String name;

        public FilteredEventsResultListener(String name,int responseCodeOk,int responseCodeError) {
            this.name = name;
            this.responseCodeOk = responseCodeOk;
            this.responseCodeError = responseCodeError;
        }

        @Override
        public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
            if (task.isSuccessful()) {
                // Creating events from data
                // Get the last visible document
                QuerySnapshot result = task.getResult();
                lastVisible = null;
                List<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : result) {
                    // Filtred by name
                    if (name == null || doc.getString("name").contains(name)){
                        lastVisible = doc;
                        events.add(getEventFrom(doc));
                    }
                }
                // Results are send to observers

                serverResponse.setValue(new ServerResponse(responseCodeOk,events,null,null));
            }
            else{
                serverResponse.setValue(new ServerResponse(responseCodeError,null,null,null));
            }
        }
    }

    public FirestoreDAOEvent(FirebaseFirestore db,MutableLiveData<ServerResponse> serverResponse) {
        this.db = db;
        this.serverResponse = serverResponse;
    }

    protected Query getFilteredQuery(Date minDate, Date maxDate, String name_org, String theme){
        // Return a query corresponding to criteria
        Query request = db.collection("events");
        if (name_org != null)
            request = request.whereEqualTo("name_organiser",name_org);
        if (theme != null)
            request = request.whereEqualTo("theme",theme);
        if (minDate != null)
            request = request.whereGreaterThanOrEqualTo("date",minDate);
        if (maxDate != null)
            request = request.whereLessThanOrEqualTo("date",maxDate);
        return request.orderBy("date");
    }

    public void getFilteredEvents(final int responseCodeOk,final int responseCodeError, int howMany, final Date minDate, final Date maxDate
            , final String name_org, final String name, final String theme, String uid){
        // Retrieve the "num" last events filtered by criteria
        final int maxVal = Math.min(howMany,20);
        lastName = name;
        if (uid != null){
            db.collection("registered").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        List<String> ids = doc.exists() ? ((List<String>) (doc.getData().get("events_registered"))) : new ArrayList<String>();
                        if (ids.isEmpty()){
                            List<Event> l = new ArrayList<>();
                            serverResponse.setValue(new ServerResponse(responseCodeOk,l,null,null));
                        }
                        else{
                            Query request = getFilteredQuery(minDate, maxDate, name_org, theme).whereIn("id_event",ids);
                            lastQuery = request;
                            request.limit(maxVal).get().addOnCompleteListener(new FilteredEventsResultListener(name,responseCodeOk,responseCodeError));
                        }

                    }
                    else{
                        serverResponse.setValue(new ServerResponse(responseCodeError,null,null,null));
                    }
                }
            });
        }
        else{
            Query request = getFilteredQuery(minDate, maxDate, name_org, theme);
            lastQuery = request;
            request.limit(maxVal).get().addOnCompleteListener(new FilteredEventsResultListener(name,responseCodeOk,responseCodeError));
        }
    }

    public void getNextEvents(final int responseCodeOk,final int responseCodeError){
        // Get the following events
        if (lastQuery != null && lastVisible != null){
            lastQuery.startAfter(lastVisible).limit(20).get().addOnCompleteListener(new FilteredEventsResultListener(lastName,responseCodeOk,responseCodeError));
        }
    }

    public void getComingEvents(int num) {
        // Retrieve the "num" last events (by date)
        getFilteredEvents(ServerResponse.RESPONSE_COMING_EVENT,ServerResponse.RESPONSE_COMING_EVENT,num,new Date(), null, null, null, null,null);
    }

    public void getRegisteredEvents(final int num, String uid) {
        getFilteredEvents(ServerResponse.RESPONSE_REGISTERED_EVENT,ServerResponse.RESPONSE_REGISTERED_EVENT,num,new Date(), null, null, null, null,uid);
    }

    public void getInfoUserOnEvent(final String uid, final String id_event){
        db.collection("ratings").document(id_event).collection("ratings")
                .document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    final int rate;
                    if (task.getResult().exists())
                        rate = task.getResult().getLong("number").intValue();
                    else
                        rate = -1;
                    db.collection("registered").document(uid).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        boolean isRegister;
                                        if (task.getResult().exists()){
                                            List<String> events = (List<String>) task.getResult().get("events_registered");
                                            isRegister = events != null && events.contains(id_event);
                                        }
                                        else
                                            isRegister = false;
                                        List<Event> events = isRegister ? new ArrayList<Event>() : null;
                                        Rating rating = null;
                                        try {
                                            rating = new Rating(uid,"",rate);
                                        } catch (RatingException ignored) {}
                                        List<Rating> ratings = new ArrayList<>();
                                        if (rating != null){
                                            ratings.add(rating);
                                        }
                                        serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_INFO_USER_EVENT_OK,events,ratings,null));
                                    }
                                    else{
                                        serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_INFO_USER_EVENT_ERROR,null,null,null));
                                    }
                                }
                            });
                }
                else {
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_INFO_USER_EVENT_ERROR,null,null,null));
                }
            }
        });

    }

    public static Event getEventFrom(QueryDocumentSnapshot doc){
        return new Event(doc.getDate("date"), doc.getString("description"), doc.getString("id_organiser")
                , doc.getString("name_organiser"), doc.getString("image_path")
                , doc.getString("name"), doc.getString("theme"),doc.getString("id_event"),doc.getLong("num_reg").intValue(),
                doc.getLong("num_rate").intValue(), doc.getDouble("avg_rate"));
    }

    public void register(String uid, String id_event) {
        Map<String, Object> map = new HashMap<>();
        map.put("events_registered", FieldValue.arrayUnion(id_event));
        db.collection("registered").document(uid).set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_REGISTER_OK,null,null,null));
                        }
                        else{
                            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_REGISTER_ERROR,null,null,null));
                        }
                    }
                });
    }

    public void unregister(String uid, String id_event) {
        Map<String, Object> map = new HashMap<>();
        map.put("events_registered", FieldValue.arrayRemove(id_event));
        db.collection("registered").document(uid).set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_UNREGISTER_OK,null,null,null));
                        }
                        else{
                            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_UNREGISTER_ERROR,null,null,null));
                        }
                    }
                });
    }

    public void getEvent(String id_event) {
        db.collection("events").whereEqualTo("id_event",id_event).get().addOnCompleteListener(
                new FilteredEventsResultListener(null,ServerResponse.RESPONSE_DETAIL_EVENT_OK,ServerResponse.RESPONSE_DETAIL_EVENT_ERROR));
    }
}
