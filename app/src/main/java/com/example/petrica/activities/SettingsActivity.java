
package com.example.petrica.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.petrica.R;
import com.example.petrica.dao.ServerResponse;

public class SettingsActivity extends BaseContentActivity{
    public static class SettingsFragment extends PreferenceFragmentCompat {
        protected EditTextPreference old_password;
        protected EditTextPreference new_password;
        protected EditTextPreference confirm_password;
        protected SettingsActivity parent;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            parent = (SettingsActivity) getActivity();
            old_password = findPreference("old_password");
            new_password = findPreference("new_password");
            confirm_password = findPreference("confirm_password");
            Preference pref_sign_out = findPreference("sign_out");
            pref_sign_out.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    parent.signOut();
                    return true;
                }
            });
            Preference pref_change_password = findPreference("change_password");
            pref_change_password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String old_p = old_password.getText();
                    final String new_p = new_password.getText();
                    String conf_p = confirm_password.getText();
                    if (!new_p.equals(conf_p)){
                        Toast.makeText(parent,R.string.settings_password_different,Toast.LENGTH_SHORT).show();
                    }
                    else{
                        parent.changePassword(old_p,new_p);
                    }
                    return true;
                }
            });
            Preference pref_delete_account = findPreference("delete_account");
            pref_delete_account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(parent);
                    adb.setMessage(R.string.settings_pref_delete_account).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                parent.deleteAccount();
                            }
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupHeader(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onServerResponse(ServerResponse serverResponse) {
        removeLoadingScreen();

    }
}