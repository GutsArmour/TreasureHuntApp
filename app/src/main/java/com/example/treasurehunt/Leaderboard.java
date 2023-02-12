package com.example.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Leaderboard extends BaseAppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    UserScores userScores = new UserScores();
    FirebaseAuth mAuth = Firebase.mAuth;
    ArrayList<UserScores> userScoresArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        ImageButton filterBtn = findViewById(R.id.filterBtn);
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Leaderboard.this, MainActivity.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        if (darkModeActive) {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Leaderboard.this, R.drawable.white_home_icon));
            filterBtn.setImageDrawable(ContextCompat.getDrawable(Leaderboard.this, R.drawable.white_filter_icon));
        }
        else {
            homeBtn.setImageDrawable(ContextCompat.getDrawable(Leaderboard.this, R.drawable.home_icon));
            filterBtn.setImageDrawable(ContextCompat.getDrawable(Leaderboard.this, R.drawable.filter_icon));
        }

        ListView ListView = (ListView) findViewById(R.id.leaderboard);
        UserScoresAdapter userScoresAdapter = new UserScoresAdapter(Leaderboard.this, R.layout.userscores, userScoresArrayList);

        boolean isAscending = false;

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot user : snapshot.getChildren()) {
                    userScores = user.getValue(UserScores.class);
                    String username = user.getKey();
                    userScores.setUsername(username);
                    userScoresArrayList.add(userScores);
//                    Collections.sort(list, new Comparator<String>() {
//                        @Override
//                        public int compare(String o1, String o2) {
//                            int count = 1;
//                            // Split the strings on the comma separator and extract the points from the second part
//                            int points1 = Integer.parseInt(o1.split(": ")[1]);
//                            int points2 = Integer.parseInt(o2.split(": ")[1]);
//                            return points2 - points1;
//                        }
//                    });
                    userScoresAdapter.notifyDataSetChanged();
                }
                ListView.setAdapter(userScoresAdapter);
//                filterBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!isAscending) {
//                            Collections.reverse(list);
//                        }
//                        else {
//                            Collections.sort(list);
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
