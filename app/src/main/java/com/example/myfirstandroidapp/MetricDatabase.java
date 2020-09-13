package com.example.myfirstandroidapp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HealthMetricsEntity.class}, version = 1)
public abstract class MetricDatabase extends RoomDatabase {
    public abstract HealthMetricDao healthMetricDao();
}
