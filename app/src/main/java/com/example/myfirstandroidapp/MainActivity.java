package com.example.myfirstandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_RECORD_TIME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);

        boolean hasCamera = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);

        Button startVideoButton = (Button) findViewById(R.id.startVideoButton);
        if(!hasCamera){
            startVideoButton.setEnabled(false);
        }

        startVideoButton.setOnClickListener(new View.OnClickListener () {

            @Override
            public void onClick(View view) {
                /*
                Context context = getApplicationContext();
                String toastMessage = "Clicked Start Video Button";
                int duration =  Toast.LENGTH_SHORT;
                Toast.makeText(context, toastMessage, duration)
                    .show();
                    */

                if(!Environment.getExternalStorageState().equals(MEDIA_MOUNTED)){
                    Toast.makeText(getApplicationContext(), "Failed to Get External Storage", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                String videoFileName = "/index-finger-video.mp4";
                File file = new File(getApplicationContext().getExternalFilesDirs(null)[0], videoFileName);
                Uri fileURI = Uri.fromFile(file);
                Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                captureVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, VIDEO_RECORD_TIME);
                captureVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
                if(captureVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(captureVideoIntent, 1);
                }
            }
        });
    }
}