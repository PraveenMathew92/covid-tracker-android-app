package com.example.myfirstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class SymptomCollectorActivity extends AppCompatActivity {
    private int [] symptoms = new int[10];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_collector);
        final HealthMetricsEntity entity = new HealthMetricsEntity();


        Button submitSymptoms = (Button) findViewById(R.id.submit_symptoms);
        submitSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MetricDatabase database = MetricDatabase.getInstance(getApplicationContext());
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent saveDBIntent = new Intent(getApplicationContext(), SaveToDatabaseService.class);
                        saveDBIntent.putExtra("symptoms", symptoms);
                        startService(saveDBIntent);
                    }
                });
            }
        });

        RatingBar symptomOneRating = (RatingBar) findViewById(R.id.symptomRating1);
        int rating = (int) symptomOneRating.getRating();

        symptomOneRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getApplicationContext(), "Selected " + rating, Toast.LENGTH_SHORT)
                        .show();
                symptoms[0] = (int) rating;
            }
        });
    }
}
