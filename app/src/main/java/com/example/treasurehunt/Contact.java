package com.example.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Contact extends BaseAppCompatActivity{

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    private EditText feedback, feedbackEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Contact.this, MainActivity.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "default value");
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        if (darkModeActive) {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Contact.this, R.drawable.white_home_icon));
        }
        else {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Contact.this, R.drawable.home_icon));
        }

        feedback = (EditText)findViewById(R.id.feedback);
        feedbackEmail = (EditText)findViewById(R.id.emailFeedback);

        Button feedbackButton = findViewById(R.id.submitFeedback);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedbackInput = feedback.getText().toString();
                String givenEmail = feedbackEmail.getText().toString();
                    databaseReference.child("users").child(username).child("feedback").child("email").setValue(givenEmail);
                    databaseReference.child("users").child(username).child("feedback").child("feedback").setValue(feedbackInput);
                Toast.makeText(Contact.this, "Thanks for the feedback", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Contact.this, MainActivity.class));
            }
        });
    }
}
