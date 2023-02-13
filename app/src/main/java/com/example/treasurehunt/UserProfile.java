package com.example.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends BaseAppCompatActivity{
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        ImageButton homeBtn = findViewById(R.id.homeBtn);

        ImageView userPfp = findViewById(R.id.userprofileImage);
        TextView userName = findViewById(R.id.userprofileUsername);
        TextView userFullname = findViewById(R.id.userprofileFullname);
        TextView userEmail = findViewById(R.id.userprofileEmail);
        TextView userPoints = findViewById(R.id.userprofilePoints);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this, MainActivity.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        if (darkModeActive) {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(UserProfile.this, R.drawable.white_home_icon));
        }
        else {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(UserProfile.this, R.drawable.home_icon));
        }
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                final String username = sharedPreferences.getString("username", "default value");
                if (snapshot.hasChild(username)) {
                    userName.setText("Username: " + username);
                    userFullname.setText("Full Name: " + snapshot.child(username).child("fullname").getValue(String.class));
                    userEmail.setText("Email: " + snapshot.child(username).child("email").getValue(String.class));
                    String points = snapshot.child(username).child("points").getValue(Long.class).toString();
                    userPoints.setText("Points: " + points);
                    String imagePfp = snapshot.child(username).child("pfp").getValue(String.class);
                    Glide.with(UserProfile.this).load(imagePfp).into(userPfp);

                    //databaseReference.child("users").child(username).child("scan_history").child(qrCodeValue).child("timestamp").setValue(timestamp);
                    //String qrCodeKey = databaseReference.child("users").child(username).child("scan_history").push().getKey();
                    //databaseReference.child("users").child(username).child("scan_history").child(qrCodeKey).child("timestamp").setValue(timestamp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
