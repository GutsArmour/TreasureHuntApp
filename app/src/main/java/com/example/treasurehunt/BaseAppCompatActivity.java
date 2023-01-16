package com.example.treasurehunt;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAppCompatActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        super.onCreate(savedInstanceState);
        if (darkModeActive) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            setTheme(R.style.darkModeText);
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            setTheme(R.style.lightModeText);
        }
    }
}
