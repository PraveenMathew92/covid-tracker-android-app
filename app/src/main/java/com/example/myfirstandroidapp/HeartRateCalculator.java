package com.example.myfirstandroidapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class HeartRateCalculator extends JobIntentService {
    public static int calculate(String filePath) {
        VideoCapture videoCapture = new VideoCapture(filePath);
        if (!videoCapture.isOpened()) {
            return -1;
        }
        List<Double> meanRedIntensities = new ArrayList<>();
        Mat image = new Mat();
        int frameCount = 0;
        while (videoCapture.grab()){
            videoCapture.retrieve(image);
            double dimension = image.size().area();
            double sum = 0;
            Mat redChannel = new Mat();
            Core.extractChannel(image, redChannel, 2);
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

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }
}
