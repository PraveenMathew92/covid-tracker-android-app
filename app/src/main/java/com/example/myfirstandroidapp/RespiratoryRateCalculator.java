package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;

import static com.example.myfirstandroidapp.SlopeAnalysis.movingAverage;
import static com.example.myfirstandroidapp.SlopeAnalysis.zeroCrossOvers;

public class RespiratoryRateCalculator extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Executors.newSingleThreadExecutor()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/CSVBreathe44.csv";
                        List<Double> accelerometerReadings = new ArrayList<>();
                        try {
                            Scanner scanner = new Scanner(new File(path));
                            while (scanner.hasNextDouble()) {
                                accelerometerReadings.add(scanner.nextDouble());
                            }
                        } catch (FileNotFoundException e) {
                            String errorMessage = "Unable to find file in " + path;
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT)
                                    .show();
                            System.out.println(errorMessage);
                        }

                        List<Double> yAxisReadings = accelerometerReadings.subList(1281, 2560);
                        List<Double> movingAverages = movingAverage(yAxisReadings, 4, 4);
                        int zeroCrossOverCount = zeroCrossOvers(movingAverages);
                        double respiratoryRate = (double) (zeroCrossOverCount * 3)/8;

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction("Respiratory Rate Receive Action");
                        broadcastIntent.putExtra("respiratoryRate", respiratoryRate);
                        getApplicationContext().sendBroadcast(broadcastIntent);
                    }
                });

        return START_STICKY;
    }
}
