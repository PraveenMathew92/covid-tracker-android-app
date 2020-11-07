package com.example.myfirstandroidapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executors;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

public class SymptomCollectorActivity extends AppCompatActivity {
    private int[] symptoms = new int[10];
    private FusedLocationProviderClient fusedLocationClient;

    private void setRatingButtonActions(RatingBar ratingBar, final int index) {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                symptoms[index] = (int) rating;
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        setContentView(R.layout.symptom_collector);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission to access the location has been granted", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(this, "Application doesn't have location permission by default", Toast.LENGTH_SHORT)
                    .show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RatingBar symptomOneRating = (RatingBar) findViewById(R.id.symptomRating1);
        RatingBar symptomTwoRating = (RatingBar) findViewById(R.id.symptomRating2);
        RatingBar symptomThreeRating = (RatingBar) findViewById(R.id.symptomRating3);
        RatingBar symptomFourRating = (RatingBar) findViewById(R.id.symptomRating4);
        RatingBar symptomFiveRating = (RatingBar) findViewById(R.id.symptomRating5);
        RatingBar symptomSixRating = (RatingBar) findViewById(R.id.symptomRating6);
        RatingBar symptomSevenRating = (RatingBar) findViewById(R.id.symptomRating7);
        RatingBar symptomEightRating = (RatingBar) findViewById(R.id.symptomRating8);
        RatingBar symptomNineRating = (RatingBar) findViewById(R.id.symptomRating9);
        RatingBar symptomTenRating = (RatingBar) findViewById(R.id.symptomRating10);

        setRatingButtonActions(symptomOneRating, 0);
        setRatingButtonActions(symptomTwoRating, 1);
        setRatingButtonActions(symptomThreeRating, 2);
        setRatingButtonActions(symptomFourRating, 3);
        setRatingButtonActions(symptomFiveRating, 4);
        setRatingButtonActions(symptomSixRating, 5);
        setRatingButtonActions(symptomSevenRating, 6);
        setRatingButtonActions(symptomEightRating, 7);
        setRatingButtonActions(symptomNineRating, 8);
        setRatingButtonActions(symptomTenRating, 9);

        checkPermissionForLocation();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        Button submitSymptoms = (Button) findViewById(R.id.submit_symptoms);
        submitSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        fusedLocationClient.getCurrentLocation(PRIORITY_BALANCED_POWER_ACCURACY, null)
                                .addOnCompleteListener(new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isCanceled()) {
                                            System.out.println("TASK CANCELLED");
                                            return;
                                        }
                                        Location location = task.getResult();
                                        Intent saveDBIntent = new Intent(getApplicationContext(), SaveToDatabaseService.class);
                                        saveDBIntent.putExtra("symptoms", symptoms);
                                        saveDBIntent.putExtra("latitude", location.getLatitude());
                                        saveDBIntent.putExtra("longitude", location.getLongitude());
                                        startService(saveDBIntent);
                                    }
                                });
                    }
                });
            }
        });

        Button uploadDatabase = (Button) findViewById(R.id.upload_database);
        uploadDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), UploadDatabaseService.class));
                startActivity(new Intent(getApplicationContext(), GetContactGraphActivity.class));
            }
        });
    }

    private void checkPermissionForLocation() {
        boolean lacksLocationPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
        if (lacksLocationPermission) {
            Toast.makeText(this, "Application doesn't have location permission", Toast.LENGTH_SHORT)
                    .show();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            Toast.makeText(this, "Has Permissions", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
