package com.example.myfirstandroidapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class HealthMetricsEntity {
    @PrimaryKey(autoGenerate = true)
    public int key;

    @ColumnInfo(name = "heart_rate")
    public float heartRate;

    @ColumnInfo(name = "respiratory_rate")
    public float respiratoryRate;

    public int symptom1;
    public int symptom2;
    public int symptom3;
    public int symptom4;
    public int symptom5;
    public int symptom6;
    public int symptom7;
    public int symptom8;
    public int symptom9;
    public int symptom10;
}
