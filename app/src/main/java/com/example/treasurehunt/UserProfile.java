package com.example.treasurehunt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UserProfile extends BaseAppCompatActivity{

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://treasurehunt-bb6e9.appspot.com");
    ImageView userPfp;
    FirebaseAuth mAuth = Firebase.mAuth;
    Uri pfpUri;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "default value");
        Boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        setContentView(R.layout.activity_userprofile);
        ImageButton homeBtn = findViewById(R.id.homeBtn);

        userPfp = findViewById(R.id.userprofileImage);
        TextView userName = findViewById(R.id.userprofileUsername);
        TextView userFullname = findViewById(R.id.userprofileFullname);
        TextView userEmail = findViewById(R.id.userprofileEmail);
        TextView userPoints = findViewById(R.id.userprofilePoints);
        Button saveProfile = findViewById(R.id.saveProfileBtn);
        Button Logout = findViewById(R.id.LogoutBtn);

        userPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery.launch("image/*");
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                sharedPreferences.edit().remove("username").apply();
                editor.apply();
                startActivity(new Intent(UserProfile.this, Login.class));
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPfp();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfile.this, MainActivity.class));
            }
        });

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
                    userName.setText(username);
                    userFullname.setText(snapshot.child(username).child("fullname").getValue(String.class));
                    userEmail.setText(snapshot.child(username).child("email").getValue(String.class));
                    userPoints.setText(snapshot.child(username).child("points").getValue(Long.class).toString());
                    Glide.with(UserProfile.this).load(snapshot.child(username).child("pfp").getValue(String.class)).into(userPfp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result != null) {
                userPfp.setImageURI(result);
                pfpUri = result;
            }
        }
    });
    private void uploadPfp() {
        if (pfpUri != null) {
            StorageReference pfpRef = storageReference.child("pfps/" + UUID.randomUUID().toString());
            pfpRef.putFile(pfpUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UserProfile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        pfpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("users").child(username).child("pfp").setValue(uri.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}
