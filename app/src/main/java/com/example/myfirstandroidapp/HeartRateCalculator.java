package com.example.myfirstandroidapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.arthenica.mobileffmpeg.FFmpeg;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class HeartRateCalculator extends Service {
    static double calculate(List<Double> meanRedIntensities) {

        List<Double> movingAverageList = movingAverage(meanRedIntensities, 5, 5);
        int zeroCrossOverCount = zeroCrossOvers(movingAverageList);
        double heartRate = (double) (zeroCrossOverCount * 3) / 8;
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
        if (list.size() < 2)
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
        convertCapturedImage();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                String path = "/storage/emulated/0/Android/data/com.example.myfirstandroidapp/files/FingertipVideo.avi";
                List<Double> averageRedIntensityPerFrame = calculateMeanRedIntensities(path);
                double heartRate = HeartRateCalculator.calculate(averageRedIntensityPerFrame);
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("Heart Rate Receive Action");
                broadcastIntent.putExtra("heartRate", heartRate);
                getApplicationContext().sendBroadcast(broadcastIntent);
            }
        });
        return START_REDELIVER_INTENT;
    }

    private List<Double> calculateMeanRedIntensities(String filePath) {
        VideoCapture videoCapture = new VideoCapture(filePath);
        if (!videoCapture.isOpened()) {
            return Collections.emptyList();
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
        return meanRedIntensities;
    }

    private void convertCapturedImage() {
        String capturedImageName = "/index-finger-video";
        String capturedImageLocation = getApplicationContext().getExternalFilesDirs(null)[0] + capturedImageName;
        int conversionOneReturnCode = FFmpeg.execute("-i " + capturedImageLocation
                + ".mp4 -vcodec mjpeg " + capturedImageLocation + ".mjpeg");
        int conversionTwoReturnCode = FFmpeg.execute("-i " + capturedImageLocation
                + ".mjpeg -vcodec mjpeg " + capturedImageLocation + ".avi");

        if (conversionOneReturnCode == RETURN_CODE_SUCCESS && conversionTwoReturnCode == RETURN_CODE_SUCCESS) {
            System.out.println("Image Converted Successfully");
        } else {
            System.out.println("Image Conversion Failed");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
