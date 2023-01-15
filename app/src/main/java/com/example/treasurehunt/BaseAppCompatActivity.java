package com.example.treasurehunt;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompatActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        super.onCreate(savedInstanceState);
        if (darkModeActive) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }
}
