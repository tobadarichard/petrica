package com.example.petrica.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {
    // Model containing data that should outlive activities
    private FirebaseAuth firebaseAuthInstance;
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<FirebaseUser>();
    private MutableLiveData<Boolean> hasConnection = new MutableLiveData<Boolean>();

    public void init(){
        if (firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance();
            firebaseAuthInstance.addAuthStateListener(this);
            user.setValue(null);
        }
    }

    public FirebaseAuth getFirebaseAuthInstance() {
        return firebaseAuthInstance;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            user.setValue(null);        }
        else{
            user.setValue(firebaseAuthInstance.getCurrentUser());
        }
    }

    public MutableLiveData<Boolean> getHasConnection() {
        return hasConnection;
    }
}
