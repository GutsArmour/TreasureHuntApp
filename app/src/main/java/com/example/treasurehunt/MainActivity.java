package com.example.treasurehunt;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Objects;

public class MainActivity extends BaseAppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth = Firebase.mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            scanQR();
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //dp
        }
        else {
            signInAnonymously();
        }

        ImageButton leaderboardMainBtn = findViewById(R.id.leaderboardMainBtn);
        leaderboardMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Leaderboard.class));
            }
        });
        ImageButton settingsBtn = findViewById(R.id.settingsBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);

        if (darkModeActive) {
            leaderboardMainBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.white_leaderboard_icon));
            settingsBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.white_settings_icon));
        }
        else {
            leaderboardMainBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.leaderboard_icon));
            settingsBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.settings_icon));
        }

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu settingsMenu = new PopupMenu(MainActivity.this, v, R.style.popupMenuDark);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu, settingsMenu.getMenu());
                settingsMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tutorialScreen:
                                startActivity(new Intent(MainActivity.this, Tutorial.class));
                                return true;
                            case R.id.scanHistoryScreen:
                                startActivity(new Intent(MainActivity.this, ScanHistory.class));
                                return true;
                            case R.id.contactScreen:
                                startActivity(new Intent(MainActivity.this, Contact.class));
                                return true;
                            case R.id.darkModeBtn:
                                SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                boolean darkModeActive = sharedPreferences.getBoolean("darkModeActive", false);
                                if (darkModeActive) {
                                    getWindow().setBackgroundDrawableResource(R.drawable.preview16);
                                    leaderboardMainBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.leaderboard_icon));
                                    settingsBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.settings_icon));
                                    editor.putBoolean("darkModeActive", false);
                                    editor.apply();
                                }
                                else {
                                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                                    leaderboardMainBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.white_leaderboard_icon));
                                    settingsBtn.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.white_settings_icon));
                                    editor.putBoolean("darkModeActive", true);
                                    editor.apply();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                settingsMenu.show();
            }
        });
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed signInAnonymously");
            }
        });
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up to turn the flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            long timestamp = System.currentTimeMillis();
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                    final String username = sharedPreferences.getString("username", "default value");
                    if (snapshot.hasChild(username)) {
                        int points = snapshot.child(username).child("points").getValue(Integer.class);
                        points += 10;
                        databaseReference.child("users").child(username).child("points").setValue(points);

                        String qrCodeValue = result.getContents();
                        databaseReference.child("users").child(username).child("scan_history").child(qrCodeValue).child("timestamp").setValue(timestamp);

                        //String qrCodeKey = databaseReference.child("users").child(username).child("scan_history").push().getKey();
                        //databaseReference.child("users").child(username).child("scan_history").child(qrCodeKey).child("timestamp").setValue(timestamp);

                        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Toast.makeText(MainActivity.this, "Permissions not granted", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // Get the longitude and latitude of the location
                                    double longitude = location.getLongitude();
                                    double latitude = location.getLatitude();

                                    // Store the longitude and latitude in the Realtime Database
                                    databaseReference.child("users").child(username).child("scan_history").child(qrCodeValue).child("longitude").setValue(longitude);
                                    databaseReference.child("users").child(username).child("scan_history").child(qrCodeValue).child("latitude").setValue(latitude);
                                }
                            }
                        });
                    }
                }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
               }
           }).show();
       }
    });
}