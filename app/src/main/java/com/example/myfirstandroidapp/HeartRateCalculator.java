package com.example.myfirstandroidapp;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class HeartRateCalculator extends Worker {
    private static final int JOB_ID = 2;

    public HeartRateCalculator(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static int calculate(String filePath) {
        VideoCapture videoCapture = new VideoCapture(filePath);
        if (!videoCapture.isOpened()) {
            return -1;
        }
        List<Double> meanRedIntensities = new ArrayList<>();
        Mat image = new Mat();
        int frameCount = 0;
        while (videoCapture.grab()){
            videoCapture.retrieve(image);
            Mat redChannel = new Mat();
            Core.extractChannel(image, redChannel, 2);
            System.out.println(++frameCount);
            meanRedIntensities.add(Core.mean(redChannel).val[0]);
        }
        movingAverage(meanRedIntensities, 8, 8);
        return 0;
    }

    private static List<Double> movingAverage(List<Double> list, int windowSize, int sampleRate) {
        List<Double> movingAverageList = new ArrayList<>();
        double sum = list.get(0);
        for(int i = 1; i < list.size(); i++) {
            double element = list.get(i);
            sum += element;
            list.set(i, sum);
        }
        for(int i = windowSize - 1; i < list.size(); i += sampleRate) {
            double movingAverage =  (list.get(i) - list.get(i - windowSize + 1))/windowSize;
            movingAverageList.add(movingAverage);
            System.out.println(movingAverage);
        }
        System.out.println(movingAverageList.size());
        return movingAverageList;
    }


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        System.out.println("IN SERVICE");
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/FingertipVideo.avi";
//                System.out.println("Begin Execution");
//                HeartRateCalculator.calculate(path);
//                stopSelf();
//            }
//        });
//        return START_STICKY;
//    }

    @NonNull
    @Override
    public Result doWork() {
        String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/FingertipVideo.avi";
        System.out.println("Begin Execution");
        HeartRateCalculator.calculate(path);
        return Result.success();
    }
}
