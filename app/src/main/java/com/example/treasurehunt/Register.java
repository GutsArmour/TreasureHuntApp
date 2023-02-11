package com.example.treasurehunt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class Register extends BaseAppCompatActivity {

    //ImageView pfp;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://treasurehunt-bb6e9.appspot.com");

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ImageView pfp = findViewById(R.id.profilePic);
        final EditText fullname = findViewById(R.id.fullname);
        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText conPassword = findViewById(R.id.conPassword);

        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginNowBtn = findViewById(R.id.loginNowBtn);
        //StorageReference defaultPfpRef = storageReference.child("pfps/default_profile_photo.jpg");

//        pfp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                launchGallery.launch(intent);
//            }
//        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullnameTxt = fullname.getText().toString();
                final String usernameTxt = username.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();
                StorageReference DefPfpRef = storageReference.child("pfps/default_profile_photo.jpg");

                if (fullnameTxt.isEmpty() || usernameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty() || conPasswordTxt.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
                else if (!passwordTxt.equals(conPasswordTxt)){
                    Toast.makeText(Register.this, "Entered Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(usernameTxt)) {
                                Toast.makeText(Register.this, "Email is already registered", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                int pointsTxt;
                                pointsTxt = 0;
                                databaseReference.child("users").child(usernameTxt).child("fullname").setValue(fullnameTxt);
                                databaseReference.child("users").child(usernameTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(usernameTxt).child("password").setValue(passwordTxt);
                                databaseReference.child("users").child(usernameTxt).child("points").setValue(pointsTxt);
                                DefPfpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        databaseReference.child("users").child(usernameTxt).child("pfp").setValue(uri.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Register.this, "Not working pfp", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                Toast.makeText(Register.this, "User Registered successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
//    ActivityResultLauncher<Intent> launchGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == Activity.RESULT_OK) {
//            Intent data = result.getData();
//            if (data != null && data.getData() != null) {
//                Uri selectedImageUri = data.getData();
//                Bitmap bitmap;
//                try {
//                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImageUri);
//                    bitmap = ImageDecoder.decodeBitmap(source);
//                    pfp.setImageBitmap(bitmap);
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    });
}