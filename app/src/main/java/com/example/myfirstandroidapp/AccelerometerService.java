package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager accelerometerSensorManager;
    private Sensor senseAccel;
    private int index = 0;
    private float accelerometerValuesX[] = new float[1028];
    private float accelerometerValuesY[] = new float[1028];
    private float accelerometerValuesZ[] = new float[1028];

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValuesX[index] = event.values[0];
            accelerometerValuesY[index] = event.values[1];
            accelerometerValuesZ[index] = event.values[2];
            index++;
            System.out.println("INDEX : " + index);
        }
        if(index > 27) {
            Toast.makeText(this, "Captured the respiratory pattern", Toast.LENGTH_SHORT)
                    .show();
            accelerometerSensorManager.unregisterListener(this);
            saveToFile();
        }
    }

    private void saveToFile() {
        String fileName = "CSVBreath.csv";
        OutputStreamWriter writer = null;
        try {
            FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(fileName, MODE_PRIVATE);
            writer = new OutputStreamWriter(fileOutputStream);
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Failed to create file to store accelerometer values", Toast.LENGTH_SHORT)
            .show();
        }
        for(float xAxisValue: accelerometerValuesX) {
            try {
                writer.write(String.valueOf(xAxisValue) + "\n");
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to write x axis paramter to file", Toast.LENGTH_SHORT)
                        .show();
            }
        }for(float yAxisValue: accelerometerValuesY) {
            try {
                writer.write(String.valueOf(yAxisValue) + "\n");
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to write y axis paramter to file", Toast.LENGTH_SHORT)
                        .show();
            }
        }for(float zAxisValue: accelerometerValuesY) {
            try {
                writer.write(String.valueOf(zAxisValue) + "\n");
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to write z axis paramter to file", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        try {
            writer.close();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to close the file", Toast.LENGTH_SHORT)
                    .show();
        }
        Toast.makeText(getApplicationContext(), "Saved accelerometer values to file " + getApplicationContext().getFilesDir() + fileName, Toast.LENGTH_SHORT)
                .show();
        System.out.println(getApplication().getFilesDir() + " LOCAL STORAGE ");

    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Accelerometer Sensor Created", Toast.LENGTH_LONG).show();
        accelerometerSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senseAccel = accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelerometerSensorManager.registerListener(this, senseAccel, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        return;
    }
}
