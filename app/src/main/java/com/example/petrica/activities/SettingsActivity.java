
package com.example.petrica.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petrica.R;
import com.example.petrica.dao.ServerResponse;

public class SettingsActivity extends BaseContentActivity{
    protected EditText old_password;
    protected EditText new_password;
    protected EditText confirm_password;
    protected TextView sign_out;
    protected TextView change_password;
    protected Button delete_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_settings);

        // Finding views
        old_password = findViewById(R.id.input_old_password);
        new_password = findViewById(R.id.input_new_password);
        confirm_password = findViewById(R.id.input_conf_password);
        sign_out = findViewById(R.id.label_sign_out);
        change_password = findViewById(R.id.label_change_password);
        delete_account = findViewById(R.id.button_delete_account);

        // Adding listeners
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_p = old_password.getText().toString();
                final String new_p = new_password.getText().toString();
                String conf_p = confirm_password.getText().toString();
                if (!new_p.equals(conf_p)){
                    Toast.makeText(SettingsActivity.this,R.string.settings_password_different,Toast.LENGTH_SHORT).show();
                }
                else{
                    changePassword(old_p,new_p);
                }
            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                adb.setMessage(R.string.settings_pref_delete_account).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            deleteAccount();
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });
    }

    @Override
    public void onServerResponse(ServerResponse serverResponse) {
        // Nothing to do
    }

    @Override
    public void onUserDisconnect() {
        // Leave
        Toast.makeText(this,R.string.sign_out_successful,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserConnect() {
        // Nothing to do
    }

    @Override
    public void refresh() {
        // Clear layout
        old_password.setText("");
        new_password.setText("");
        confirm_password.setText("");
    }
}