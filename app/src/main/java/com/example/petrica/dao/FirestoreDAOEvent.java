package com.example.petrica.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.petrica.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FirestoreDAOEvent {
    protected FirebaseFirestore db;
    protected MutableLiveData<List<Event>> comingEvents;
    protected MutableLiveData<List<Event>> followedEvents;
    protected MutableLiveData<List<Event>> searchEvents;
    protected DocumentSnapshot lastVisible;
    protected Query lastQuery;
    protected String lastName;

    private class FilteredEventsResultListener implements OnCompleteListener<QuerySnapshot>{
        private int maxVal;
        private  MutableLiveData<List<Event>> whichEvents;
        private String name;
        private boolean mustErase;

        public FilteredEventsResultListener(int maxVal,MutableLiveData<List<Event>> whichEvents,String name,boolean mustErase) {
            this.whichEvents = whichEvents;
            this.maxVal = maxVal;
            this.name = name;
            this.mustErase = mustErase;
        }

        @Override
        public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
            if (task.isSuccessful()) {
                // Creating events from data
                // Get the last visible document
                QuerySnapshot result = task.getResult();
                if (result.size() < maxVal){
                    lastVisible = null;
                }
                else{
                    lastVisible = result.getDocuments()
                            .get(result.size() -1);
                }
                List<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    // Filtred by name
                    if (name == null || doc.getString("name").contains(name)){
                        events.add(Event.getFrom(doc));
                    }
                }
                // Results are send to observers
                if (mustErase){
                    whichEvents.setValue(null);
                }
                whichEvents.setValue(events);
            }
            else{
                if (mustErase){
                    whichEvents.setValue(null);
                }
                else{
                    whichEvents.setValue(new ArrayList<Event>());
                }
            }
        }
    }

    public FirestoreDAOEvent(FirebaseFirestore db, MutableLiveData<List<Event>> observedEvents, MutableLiveData<List<Event>> followedEvents, MutableLiveData<List<Event>> searchEvents) {
        this.db = db;
        this.comingEvents = observedEvents;
        this.followedEvents = followedEvents;
        this.searchEvents = searchEvents;
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

    public void getFilteredEvents(final MutableLiveData<List<Event>> whichEvents, int howMany, final Date minDate, final Date maxDate
            , final String name_org, final String name, final String theme, String uid, final boolean mustErase){
        // Retrieve the "num" last events filtered by criteria
        final int maxVal = Math.min(howMany,20);
        lastName = name;
        if (uid != null){
            db.collection("followed").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        List<String> ids = doc.exists() ? ((List<String>) (doc.getData().get("events_followed"))) : new ArrayList<String>();
                        Query request = getFilteredQuery(minDate, maxDate, name_org, theme).whereIn("id_event",ids);
                        lastQuery = request;
                        request.limit(maxVal).get().addOnCompleteListener(new FilteredEventsResultListener(maxVal,whichEvents,name,mustErase));
                    }
                    else{
                        whichEvents.setValue(new ArrayList<Event>());
                    }
                }
            });
        }
        else{
            Query request = getFilteredQuery(minDate, maxDate, name_org, theme);
            lastQuery = request;
            request.limit(maxVal).get().addOnCompleteListener(new FilteredEventsResultListener(maxVal,whichEvents,name,mustErase));
        }
    }

    public void getNextEvents(MutableLiveData<List<Event>> whichEvents){
        // Get the following events
        if (lastQuery != null && lastVisible != null){
            lastQuery.startAfter(lastVisible).limit(20).get().addOnCompleteListener(new FilteredEventsResultListener(20,whichEvents,lastName,false));
        }
    }

    public void getComingEvents(int num) {
        // Retrieve the "num" last events (by date)
        getFilteredEvents(comingEvents,num,new Date(), null, null, null, null,null,true);
    }

    public void getFollowedEvents(final int num, String uid) {
        getFilteredEvents(followedEvents,num,new Date(), null, null, null, null,uid,true);
    }
}
