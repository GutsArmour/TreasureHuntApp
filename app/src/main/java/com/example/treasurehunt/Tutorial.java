package com.example.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

public class Tutorial extends BaseAppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Tutorial.this, MainActivity.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);
        if (darkModeActive) {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Tutorial.this, R.drawable.white_home_icon));
        }
        else {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Tutorial.this, R.drawable.home_icon));
        }
    }
}
