package com.example.myfirstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class SymptomCollectorActivity extends AppCompatActivity {
    private int[] symptoms = new int[10];

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
        setContentView(R.layout.symptom_collector);
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
                        finishAffinity();
                    }
                });
            }
        });
    }
}
