package com.example.petrica.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.petrica.R;
import com.example.petrica.model.MyViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public abstract class AuthenticationActivity extends AppCompatActivity {
    // Activity used to ensure authentication
    protected MyViewModel model;
    protected FirebaseUser user;

    // Activity results code for all activities
    protected static final int RESULT_SIGN_IN = 1;

    // Permission requests code
    public static final int PERMISSION_NETWORK = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recovering the view model
        model = new ViewModelProvider(this).get(MyViewModel.class);
        model.init();
        user = model.getUser().getValue();
        model.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                user = firebaseUser;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Authentication successful
                model.getUser();
                Toast.makeText(this, getString(R.string.success_sign), Toast.LENGTH_SHORT).show();
            } else {
                // Authentication failed
                if (response != null) {
                    Toast.makeText(this, getString(R.string.err_retry), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    protected void startLogin(){
        if (user != null){
            Toast.makeText(this,getString(R.string.already_login),Toast.LENGTH_SHORT).show();
        }
        else{
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.AppTheme)
                            .build(),
                    RESULT_SIGN_IN);
        }
    }

    protected void askLogin(){
        // Asking for login
        if (user != null){
            // Doing nothing
            return;
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.need_sign_title))
                .setMessage(R.string.need_sign)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startLogin();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        adb.create().show();
    }


}
