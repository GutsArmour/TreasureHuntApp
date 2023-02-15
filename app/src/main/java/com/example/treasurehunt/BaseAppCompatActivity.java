package com.example.treasurehunt;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BaseAppCompatActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        super.onCreate(savedInstanceState);
        if (darkModeActive) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            setTheme(R.style.darkModeText);
        } else {
            int peachColor = ContextCompat.getColor(this, R.color.peach);
            getWindow().getDecorView().setBackgroundColor(peachColor);
            setTheme(R.style.lightModeText);
        }
    }
}
