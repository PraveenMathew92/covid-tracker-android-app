package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class HeartRateCalculator extends Service {
    static double calculate(String filePath) {
        VideoCapture videoCapture = new VideoCapture(filePath);
        if (!videoCapture.isOpened()) {
            return -1;
        }
        List<Double> meanRedIntensities = new ArrayList<>();
        Mat image = new Mat();
        Mat redChannel = new Mat();
        while (videoCapture.read(image)) {
            videoCapture.read(image);
            Core.extractChannel(image, redChannel, 2);
            double frameRedIntensityMean = Core.mean(redChannel).val[0];
            meanRedIntensities.add(frameRedIntensityMean);
        }
        List<Double> movingAverageList = movingAverage(meanRedIntensities, 5, 5);

        int sampleCount = 9;
        int sampleSize = movingAverageList.size()/sampleCount;
        int zeroCrossOverCount = 0;
//        for(int i = sampleSize; i < movingAverageList.size(); i += sampleSize){
//            int zeroCrossOverInSample = zeroCrossOvers(movingAverageList.subList(i - sampleSize, i));
//            System.out.println(zeroCrossOverInSample);
//            zeroCrossOverCount += zeroCrossOverInSample;
//        }
        zeroCrossOverCount = zeroCrossOvers(movingAverageList);
        double averageZeroCrossOvers = (double) (zeroCrossOverCount)/sampleCount;

//        average * 1/2 * sampleCount *  3/4

        double heartRate = (double) (zeroCrossOverCount * 3)/8;
        return heartRate;
    }

    public static List<Double> movingAverage(List<Double> list, int windowSize, int sampleRate) {
        List<Double> movingAverageList = new ArrayList<>();
        double sum = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            double element = list.get(i);
            sum += element;
            list.set(i, sum);
        }
        for (int i = windowSize; i < list.size(); i += sampleRate) {
            double movingAverage = (list.get(i) - list.get(i - windowSize)) / windowSize;
            movingAverageList.add(movingAverage);
        }
        return movingAverageList;
    }

    public static int zeroCrossOvers(List<Double> list) {
        if(list.size() < 2)
            return 0;
        double previousElement;
        double nextElement = list.get(1);
        double currentElement = list.get(0);
        int crossOverCount = 0;
        for (int i = 1; i < list.size() - 1; i++) {
            previousElement = currentElement;
            currentElement = nextElement;
            nextElement = list.get(i + 1);
            boolean isTrough = previousElement >= currentElement && currentElement < nextElement;
            boolean isCrest = previousElement <= currentElement && currentElement > nextElement;
            if (isTrough || isCrest)
                crossOverCount++;
        }
        return crossOverCount;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/FingertipVideo.avi";
                double heartRate = HeartRateCalculator.calculate(path);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("Heart Rate Receive Action");
                broadcastIntent.putExtra("heartRate", heartRate);
                getApplicationContext().sendBroadcast(broadcastIntent);
            }
        });
        return START_REDELIVER_INTENT;
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, HeartRateCalculator.class);
//        this.sendBroadcast(broadcastIntent);
//    }

    public class HeartRateBinder extends Binder {
        HeartRateCalculator getService() {
            return HeartRateCalculator.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
