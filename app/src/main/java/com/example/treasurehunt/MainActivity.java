package com.example.treasurehunt;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.net.HttpCookie;


public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button;
        button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            scanQR();
        });

        ImageButton leaderboardMainBtn = findViewById(R.id.leaderboardMainBtn);
        leaderboardMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Leaderboard.class));
            }
        });
        ImageButton settingsBtn = findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu settingsMenu = new PopupMenu(MainActivity.this, v);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu, settingsMenu.getMenu());
                settingsMenu.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tutorialScreen:
                Toast.makeText(this, "Tutorial screen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.scanHistoryScreen:
                Toast.makeText(this, "Scan History screen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.contactScreen:
                Toast.makeText(this, "Contact screen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.darkModeBtn:
                Toast.makeText(this, "Dark Mode option", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press volume up to turn the flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
       if (result.getContents() != null) {
           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
           builder.setTitle("Result");
           builder.setMessage(result.getContents());
           databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
                   final String username = sharedPreferences.getString("username", "default value");
                   if (snapshot.hasChild(username)) {
                       int points = snapshot.child(username).child("points").getValue(Integer.class);
                       points += 10;
                       databaseReference.child("users").child(username).child("points").setValue(points);
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