package com.example.myfirstandroidapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.Objects;

@Database(entities = {HealthMetricsEntity.class}, version = 1)
public abstract class MetricDatabase extends RoomDatabase {
    private static MetricDatabase database;

    public static synchronized void createDatabase(Context context, String lastname) {
        database = Room.databaseBuilder(context, MetricDatabase.class, "database-name")
                .build();
    }

    public static synchronized MetricDatabase getInstance(Context context) {
        return database;
    }

    public abstract HealthMetricDao healthMetricDao();
}
