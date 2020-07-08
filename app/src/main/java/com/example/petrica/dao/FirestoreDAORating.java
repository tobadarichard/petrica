package com.example.petrica.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.petrica.model.Rating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDAORating {
    protected MutableLiveData<ServerResponse> serverResponse;
    protected FirebaseFirestore db;


    public FirestoreDAORating(FirebaseFirestore db, MutableLiveData<ServerResponse> serverResponse){
        this.db = db;
        this.serverResponse = serverResponse;
    }

    public void rate(final Rating rating, String id_event){
        db.collection("ratings").document(id_event).collection("ratings")
                .document(rating.getId_user()).set(ratingToData(rating)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    List<Rating> ratingList = new ArrayList<>();
                    ratingList.add(rating);
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_WRITING_RATING_OK,null,ratingList,null));
                }
                else{
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_WRITING_RATING_ERROR,null,null,null));
                }
            }
        });
    }

    public void unrate(String uid, String id_event){
        db.collection("ratings").document(id_event).collection("ratings")
                .document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_DELETE_RATING_OK,null,null,null));
                }
                else{
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_DELETE_RATING_ERROR,null,null,null));
                }
            }
        });
    }

    public static Map<String,Object> ratingToData(Rating rating) {
        Map<String,Object> result = new HashMap<>();
        result.put("id_user",rating.getId_user());
        result.put("name_user",rating.getName_user());
        result.put("number",rating.getNumber());
        return result;
    }
}
