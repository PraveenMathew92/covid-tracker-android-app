package com.example.myfirstandroidapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HealthMetricsEntity.class}, version = 1)
public abstract class MetricDatabase extends RoomDatabase {
    private static MetricDatabase database;
    private static String databaseName;

    public static synchronized void createDatabase(Context context, String lastname) {
        databaseName = lastname;
        database = Room.databaseBuilder(context, MetricDatabase.class, lastname)
                .build();
    }

    public static synchronized MetricDatabase getInstance(Context context) {
        return database;
    }

    public static synchronized String getName(Context context) {
        return databaseName;
    }

    public abstract HealthMetricDao healthMetricDao();
}
