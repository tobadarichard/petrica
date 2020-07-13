package com.example.petrica.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.petrica.model.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDAOComment {
    protected FirebaseFirestore db;
    protected MutableLiveData<ServerResponse> serverResponse;
    protected DocumentSnapshot lastVisible;
    protected Query lastQuery;

    public FirestoreDAOComment(FirebaseFirestore db,MutableLiveData<ServerResponse> serverResponse) {
        this.db = db;
        this.serverResponse = serverResponse;
    }

    public void getComments(String id_event,String id_user,int howMany){
        Query q;
        if (id_user == null){
            q = db.collection("reviews").document(id_event).collection("reviews");
        }
        else {
            q = db.collection("reviews").document(id_event).collection("reviews").whereEqualTo("id_user",id_user);
        }
        lastQuery = q;
        int maxVal = Math.min(howMany,20);
        if (maxVal > 0){
            q = q.limit(maxVal);
        }
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Comment> commentList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        commentList.add(commentFrom(doc));
                        lastVisible = doc;
                    }
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_COMMENT_EVENT_OK,null,null,commentList));
                }
                else {
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_COMMENT_EVENT_ERROR,null,null,null));
                }
            }
        });
    }

    public void getNextComments(int howMany){
        if (lastQuery != null && lastVisible != null){
            lastQuery.startAfter(lastVisible).limit(howMany).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        List<Comment> commentList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            commentList.add(commentFrom(doc));
                            lastVisible = doc;
                        }
                        serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_COMMENT_EVENT_NEXT,null,null,commentList));
                    }
                    else {
                        serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_COMMENT_EVENT_ERROR,null,null,null));
                    }
                }
            });
        }
        else if (lastQuery == null){
            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_TO_IGNORE,null,null,null));
        }
        else{
            List<Comment> commentList = new ArrayList<>();
            serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_COMMENT_EVENT_NEXT,null,null,commentList));
        }
    }

    public void writeComment(final Comment c, String id_event) {
        String id = db.collection("reviews").document(id_event).collection("reviews").document().getId();
        c.setId_comment(id);
        db.collection("reviews").document(id_event).collection("reviews").document(id)
                .set(commentToData(c)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    List<Comment> commentList = new ArrayList<>();
                    commentList.add(c);
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_WRITING_COMMENT_OK,null,null,commentList));
                }
                else{
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_WRITING_COMMENT_ERROR,null,null,null));
                }
            }
        });
    }

    public void unwriteComment(final String id_doc_comment, String id_event) {
        db.collection("reviews").document(id_event).collection("reviews")
                .document(id_doc_comment).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    List<Comment> commentList = new ArrayList<>();
                    commentList.add(new Comment(null,null,null,null,id_doc_comment));
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_DELETE_COMMENT_OK,null,null,commentList));
                }
                else{
                    serverResponse.setValue(new ServerResponse(ServerResponse.RESPONSE_DELETE_COMMENT_ERROR,null,null,null));
                }
            }
        });
    }

    public static Map<String,Object> commentToData(Comment comment) {
        Map<String,Object> result = new HashMap<>();
        result.put("id_user",comment.getId_user());
        result.put("name_user",comment.getName_user());
        result.put("date",comment.getDate());
        result.put("id_comment",comment.getId_comment());
        result.put("message",comment.getMessage());
        return result;
    }

    public static Comment commentFrom(QueryDocumentSnapshot doc){
        return new Comment(doc.getDate("date"),doc.getString("id_user"),doc.getString("name_user"),
                doc.getString("message"),doc.getString("id_comment"));
    }
}
