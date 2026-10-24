package com.example.smartpantry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SwitchCompat sw = findViewById(R.id.swExpiry);
        sw.setChecked(prefs.getBoolean("expiry_alerts", true));

        sw.setOnCheckedChangeListener((button, checked) ->
                prefs.edit().putBoolean("expiry_alerts", checked).apply());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
