package com.example.petrica.activities;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.petrica.R;

public class FirstActivity extends AuthenticationActivity {
    protected TextView logo;
    protected TextView main_label;
    protected Button button_continue;
    protected Button button_login;
    protected boolean isAnimating;
    protected ViewPropertyAnimator animate1;
    protected ViewPropertyAnimator animate2;
    protected ViewPropertyAnimator animate3;
    protected ViewPropertyAnimator animate4;

    public static class AnimationViewListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animation.end();
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        isAnimating = false;
        ConstraintLayout cl = findViewById(R.id.first_layout);
        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isAnimating){
                    animate1.cancel();
                    animate2.cancel();
                    animate3.cancel();
                    animate4.cancel();
                    return true;
                }
                return false;
            }
        });

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
                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                if (user != null){
                    FirstActivity.this.finish();
                }
            }
        });
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });
        // Views are visible but transparent by default
        make_animation(savedInstanceState != null);
    }

    @Override
    public void onUserDisconnect() {
        // Nothing to do
    }

    @Override
    public void onUserConnect() {
        // Nothing to do
    }

    public void make_animation(boolean quickMode) {
        animate1 = logo.animate();
        animate2 = main_label.animate();
        animate3 = button_login.animate();
        animate4 = button_continue.animate();
        int orientation = getResources().getConfiguration().orientation;
        int duration1 = 2000;
        int duration2 = 1000;
        int delay =  500;
        int trans1 = -500;
        int trans2 = -500;
        int trans3 = -400;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            trans1 /= 2;
            trans2 /= 2;
            trans3 /= 2;
        }
        isAnimating = true;
        // Starting animate
        // Every view are appearing progressively

        animate1.setStartDelay(delay)
                .translationYBy(trans1)
                .setDuration(duration1)
                .alpha(1).setListener(new AnimationViewListener());
        animate1.start();

        animate2.setStartDelay(delay + duration1)
                .translationYBy(trans2)
                .setDuration(duration1)
                .alpha(1).setListener(new AnimationViewListener());

        animate2.start();

        animate3.setStartDelay(delay + 2*duration1)
                .setDuration(duration2)
                .translationYBy(trans3)
                .alpha(1).setListener(new AnimationViewListener());

        animate3.start();

        animate4.setStartDelay(delay + 2*duration1)
                .setDuration(duration2)
                .translationYBy(trans3)
                .alpha(1).setListener(new AnimationViewListener());
        animate4.withEndAction(new Runnable() {
            @Override
            public void run() {
                isAnimating = false;
                if (user != null){
                    button_continue.callOnClick();
                }
            }
        });
        animate4.start();
        if (quickMode){
            animate1.cancel();
            animate2.cancel();
            animate3.cancel();
            animate4.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_NETWORK){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED){
                // Permissions have not been granted : closing app
                Toast.makeText(this,R.string.err_permissions,Toast.LENGTH_LONG).show();
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