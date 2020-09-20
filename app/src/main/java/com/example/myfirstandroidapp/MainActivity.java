package com.example.myfirstandroidapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

public class MainActivity extends AppCompatActivity {

    private static final int VIDEO_RECORD_TIME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenCVLoader.initDebug();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);

        boolean hasCamera = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);

        Button startVideoButton = (Button) findViewById(R.id.startVideoButton);
        if (!hasCamera) {
            startVideoButton.setEnabled(false);
        }

        startVideoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!Environment.getExternalStorageState().equals(MEDIA_MOUNTED)) {
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

                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = "";
                try {
                    String[] cameraIds = cameraManager.getCameraIdList();
                    for (String id : cameraIds) {
                        if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                            cameraId = id;
                            break;
                        }
                    }
                    if (cameraId.equals("")) {
                        Toast.makeText(getApplicationContext(), "No Camera with Flash Found", Toast.LENGTH_SHORT)
                        .show();
                    }

                } catch (CameraAccessException e) {
                    Toast.makeText(getApplicationContext(), "Failed To Get Camera For Flash", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (captureVideoIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        if (!cameraId.equals(""))
                            cameraManager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException e) {
                        Toast.makeText(getApplicationContext(), "Unable to turn on camera", Toast.LENGTH_SHORT)
                                .show();
                    }
                    startActivityForResult(captureVideoIntent, 1);
                    Toast.makeText(getApplicationContext(), "Saved to " + fileURI.toString(), Toast.LENGTH_SHORT)
                            .show();
                    try {
                        if (!cameraId.equals(""))
                            cameraManager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/FingertipVideo.avi";
        HeartRateCalculator.calculate(path);

        Button measureRespiratoryRateButton = (Button) findViewById(R.id.respiratory_rate_measure_button);
        measureRespiratoryRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, SymptomCollectorActivity.class));
            }
        });

        Button captureSymptoms = (Button) findViewById(R.id.captureSymptoms);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText inputField = new EditText(this);
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputField.setHint("LastName");

        captureSymptoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) { inputField.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogBuilder
                        .setTitle("Enter Last Name")
                        .setView(inputField)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String lastname = inputField.getText().toString();
                                Toast.makeText(getApplicationContext(), "Saved name " + lastname, Toast.LENGTH_SHORT)
                                        .show();
                                MetricDatabase.createDatabase(getApplicationContext(), lastname);
                                startActivity(new Intent(MainActivity.this, SymptomCollectorActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
    }
}