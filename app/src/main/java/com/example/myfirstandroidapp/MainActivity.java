package com.example.myfirstandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startVideoButton = (Button) findViewById(R.id.startVideoButton);
        startVideoButton.setOnClickListener(new View.OnClickListener () {

            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                String toastMessage = "Clicked Start Video Button";
                int duration =  Toast.LENGTH_SHORT;
                Toast.makeText(context, toastMessage, duration)
                    .show();
            }
        });
    }
}