package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;

public class SaveToDatabaseService extends Service {
    private HealthMetricsEntity entity = new HealthMetricsEntity();
    private boolean waitingForHeartRate = true;
    private boolean waitingForRespiratoryRate = true;

    private BroadcastReceiver heartRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            entity.heartRate = (float) intent.getDoubleExtra("heartRate", 0);
            waitingForHeartRate = false;
        }
    };

    private BroadcastReceiver respiratoryRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            entity.respiratoryRate = (float) intent.getDoubleExtra("respiratoryRate", 0);
            waitingForRespiratoryRate = false;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void saveDatabase(int[] symptoms, double latitude, double longitude) {
        populateSymptoms(symptoms);
        entity.latitude = latitude;
        entity.longitude = longitude;
        Executors.newSingleThreadExecutor()
                .execute(new Runnable() {
                    final MetricDatabase database = MetricDatabase.getInstance(getApplicationContext());
                    @Override
                    public void run() {
                        while (waitingForHeartRate || waitingForRespiratoryRate) {}
                        System.out.println("ENTITY:\n" + entity.toString());
                        database.healthMetricDao()
                                .insert(entity);
                    }
                });
    }

    private void populateSymptoms(int[] symptoms) {
        entity.symptom1 = symptoms[0];
        entity.symptom2 = symptoms[1];
        entity.symptom3 = symptoms[2];
        entity.symptom4 = symptoms[3];
        entity.symptom5 = symptoms[4];
        entity.symptom6 = symptoms[5];
        entity.symptom7 = symptoms[6];
        entity.symptom8 = symptoms[7];
        entity.symptom9 = symptoms[8];
        entity.symptom10 = symptoms[9];
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getApplicationContext().registerReceiver(heartRateReceiver, new IntentFilter("Heart Rate Receive Action"));
        getApplicationContext().registerReceiver(respiratoryRateReceiver, new IntentFilter("Respiratory Rate Receive Action"));
        startService(new Intent(getApplicationContext(), HeartRateCalculator.class));
        startService(new Intent(getApplicationContext(), RespiratoryRateCalculator.class));
        int[] symptoms = intent.getIntArrayExtra("symptoms");
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        saveDatabase(symptoms, latitude, longitude);
        return START_STICKY;
    }
}
