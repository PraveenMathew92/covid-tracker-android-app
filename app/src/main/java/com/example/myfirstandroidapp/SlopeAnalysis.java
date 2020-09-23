package com.example.myfirstandroidapp;

import java.util.ArrayList;
import java.util.List;

public class SlopeAnalysis {
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
            boolean isTrough = previousElement > currentElement && currentElement < nextElement;
            boolean isCrest = previousElement < currentElement && currentElement > nextElement;
            if (isTrough || isCrest)
                crossOverCount++;
        }
        return crossOverCount;
    }
}
