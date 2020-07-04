package com.example.petrica.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petrica.R;

public class MainActivity extends AuthenticationActivity {
    protected TextView logo;
    protected TextView main_label;
    protected Button button_continue;
    protected Button button_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asking for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET},PERMISSION_NETWORK);
        }

        logo = findViewById(R.id.logo_label);
        button_continue = findViewById(R.id.button_continue);
        button_login = findViewById(R.id.button_login);
        main_label = findViewById(R.id.main_label);

        // Adding listeners
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
                if (user != null){
                    button_continue.callOnClick();
                }
            }
        });

        
        // Views are visible but transparent by default
        make_animation(logo.animate(),main_label.animate(),button_login.animate(),button_continue.animate(),savedInstanceState != null);

    }

    private void make_animation(ViewPropertyAnimator animate1, ViewPropertyAnimator animate2, ViewPropertyAnimator animate3, ViewPropertyAnimator animate4,boolean quickMode) {
        int orientation = getResources().getConfiguration().orientation;
        int duration1 = quickMode ? 10 : 2000;
        int duration2 = quickMode ? 10 : 1000;
        int delay =  quickMode ? 10 : 500;
        int trans1 = -500;
        int trans2 = -500;
        int trans3 = -400;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            trans1 /= 2;
            trans2 /= 2;
            trans3 /= 2;
        }

        // Starting animate
        // Every view are appearing progressively

        animate1.setStartDelay(delay)
                .translationYBy(trans1)
                .setDuration(duration1)
                .alpha(1);
        animate1.start();

        animate2.setStartDelay(delay + duration1)
                .translationYBy(trans2)
                .setDuration(duration1)
                .alpha(1);

        animate2.start();

        animate3.setStartDelay(delay + 2*duration1)
                .setDuration(duration2)
                .translationYBy(trans3)
                .alpha(1);

        animate3.start();

        animate4.setStartDelay(delay + 2*duration1)
                .setDuration(duration2)
                .translationYBy(trans3)
                .alpha(1);

        animate4.withEndAction(new Runnable() {
            @Override
            public void run() {
                if (user != null){
                    button_continue.callOnClick();
                }
            }
        });
        animate4.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_NETWORK){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED){
                // Permissions have not been granted : closing app
                finish();
            }
            else {
                // Checking connection
                ConnectivityManager connMgr =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean conn = connMgr.getActiveNetworkInfo() == null;
                model.getHasConnection().setValue(conn);
            }

        }
    }
}