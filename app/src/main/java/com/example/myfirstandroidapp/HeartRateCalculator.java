package com.example.myfirstandroidapp;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HeartRateCalculator {
    public static int calculate(String filePath) {
        VideoCapture videoCapture = new VideoCapture(filePath);
        if (!videoCapture.isOpened()) {
            return -1;
        }
        List<Integer> meanRedIntensities = new ArrayList<>();
        Mat image = new Mat();
        while (videoCapture.grab()){
            videoCapture.retrieve(image);
            double dimension = image.size().area();
            double sum = 0;
            for (int i = 0; i < image.rows(); i++) {
                for (int j = 0; j < image.cols(); j++) {
                    double[] pixels = image.get(i, j);
                    sum+= pixels[2];
                }
            }
            meanRedIntensities.add((int) (sum/dimension));
        }
        return 0;
    }
}
