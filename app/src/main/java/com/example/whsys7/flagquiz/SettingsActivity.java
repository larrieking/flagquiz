package com.example.whsys7.flagquiz;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final  String AFRICA = "africa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.xml.preference);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

    }
}
