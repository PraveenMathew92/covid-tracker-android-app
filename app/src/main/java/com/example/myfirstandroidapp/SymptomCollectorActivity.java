package com.example.myfirstandroidapp;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SymptomCollectorActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.symptom_collector);

        RatingBar symptomOneRating = (RatingBar) findViewById(R.id.symptomRating1);
        int rating = (int) symptomOneRating.getRating();

        symptomOneRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(getApplicationContext(), "Selected " + rating, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
