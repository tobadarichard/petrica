package com.example.petrica.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.petrica.R;
import com.example.petrica.model.MyViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public abstract class AuthenticationActivity extends AppCompatActivity {
    // Activity used to ensure authentication
    protected MyViewModel model;
    protected FirebaseUser user;

    // Activity results code for all activities
    protected static final int RESULT_SIGN_IN = 1;

    // Activity results code for all activities
    protected static final int RESULT_DELETE_ACCOUNT = 2;

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
        if (requestCode == RESULT_SIGN_IN || requestCode == RESULT_DELETE_ACCOUNT) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Authentication successful
                model.getUser();
                if (requestCode == RESULT_SIGN_IN){
                    if (user.getDisplayName() == null){
                        Toast.makeText(this, getString(R.string.err_retry), Toast.LENGTH_LONG).show();
                        model.getFirebaseAuthInstance().signOut();
                        model.getUser().setValue(null);
                    }
                    else{
                        Toast.makeText(this, getString(R.string.success_sign), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(AuthenticationActivity.this,R.string.settings_delete_account_successful, Toast.LENGTH_SHORT).show();
                                model.getFirebaseAuthInstance().signOut();
                            }
                            else{
                                Toast.makeText(AuthenticationActivity.this, getString(R.string.err_retry), Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    });
                }
            } else {
                // Authentication failed
                if (response != null) {
                    Toast.makeText(this, getString(R.string.err_retry), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void startLogin(){
        startLogin(RESULT_SIGN_IN);
    }

    public void startLogin(int REQUEST_CODE){
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
                    REQUEST_CODE);
        }
    }

    public void askLogin(){
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

    public void signOut(){
        if (user != null){
            model.getFirebaseAuthInstance().signOut();
            model.getUser();
        }
        if (user == null){
            Toast.makeText(this,R.string.sign_out_successful,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, R.string.err_retry, Toast.LENGTH_SHORT).show();
        }
    }

    public void changePassword(String old_p, final String new_p){
        if (user == null){
            askLogin();
        }
        else if (!user.getProviderId().equals(EmailAuthProvider.PROVIDER_ID)){
            Toast.makeText(this,getString(R.string.settings_password_incorrect_provider,user.getProviderId())
                    ,Toast.LENGTH_SHORT).show();
        }
        else if (new_p.length() < 6 || !new_p.contains("123456789")){
            Toast.makeText(this,R.string.settings_password_weak,Toast.LENGTH_LONG).show();
        }
        else{
            AuthCredential cred = EmailAuthProvider.getCredential(user.getEmail(),old_p);
            user.reauthenticate(cred).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        user.updatePassword(new_p).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AuthenticationActivity.this,R.string.settings_password_change_successful,Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(AuthenticationActivity.this,R.string.settings_password_change_unsuccessful,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(AuthenticationActivity.this,R.string.err_retry,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void deleteAccount(){
        model.getFirebaseAuthInstance().signOut();
        model.getUser();
        startLogin(RESULT_DELETE_ACCOUNT);
    }

}
