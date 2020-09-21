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
        }
        Toast.makeText(this, "Captured the respiratory pattern", Toast.LENGTH_SHORT)
                .show();
        if(index > 1027) {
            accelerometerSensorManager.unregisterListener(this);
            for (int i = 0; i < 1028; i++){
                System.out.println(accelerometerValuesX[i] + " " + accelerometerValuesY[i] + " " + accelerometerValuesZ[i]);
            }
        }
    }

    private void saveToFile() {
        String fileName = "CSVBreath.csv";
        File file = new File(getApplicationContext().getExternalFilesDirs(null)[0], fileName);
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
