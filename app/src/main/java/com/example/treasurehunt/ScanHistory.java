package com.example.treasurehunt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScanHistory extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://treasurehunt-bb6e9-default-rtdb.firebaseio.com/");
    ArrayList<String> scanTimeLocationList = new ArrayList<>();
    ScanTimeLocation scanTimeLocation = new ScanTimeLocation();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanhistory);

        ListView ListView = (ListView) findViewById(R.id.scanHistory);
        ArrayAdapter<String> adapter =  new ArrayAdapter<>(ScanHistory.this, R.layout.scantimelocation, R.id.scanTimeLocation, scanTimeLocationList);;

        SharedPreferences sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "default value");

        databaseReference.child("users").child(username).child("scan_history").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    scanTimeLocation = childSnapshot.getValue(ScanTimeLocation.class);
                    String scanLocation  = (scanTimeLocation.getLongitude() + "," + scanTimeLocation.getLatitude());
                    scanTimeLocationList.add(scanTimeLocation.getTimestamp().toString() + "," + scanLocation);
//                    Long scanTime = childSnapshot.child("timestamp").getValue(Long.class);
//                    Double scanLongitude = childSnapshot.child("longitude").getValue(Double.class);
//                    Double scanLatitude = childSnapshot.child("latitude").getValue(Double.class);
//                    scanTimeLocationList.add(scanTime + "," + scanLocation);
                    adapter.notifyDataSetChanged();
                }
                ListView.setAdapter(adapter);
                SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.mapContainer, mapFragment).commit();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        for (String scanTimeLocation : scanTimeLocationList) {
                            String[] parts = scanTimeLocation.split(",");
                            String scanTime = parts[0];
                            double scanLongitude = Double.parseDouble(parts[1]);
                            double scanLatitude = Double.parseDouble(parts[2]);

                            LatLng scanLocation = new LatLng(scanLatitude, scanLongitude);
                            googleMap.addMarker(new MarkerOptions().position(scanLocation).title(scanTime));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
