package com.example.myfirstandroidapp;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @Override
    public String toString() {
        return "HealthMetricsEntity{" +
                "key=" + key +
                ", heartRate=" + heartRate +
                ", respiratoryRate=" + respiratoryRate +
                ", symptom1=" + symptom1 +
                ", symptom2=" + symptom2 +
                ", symptom3=" + symptom3 +
                ", symptom4=" + symptom4 +
                ", symptom5=" + symptom5 +
                ", symptom6=" + symptom6 +
                ", symptom7=" + symptom7 +
                ", symptom8=" + symptom8 +
                ", symptom9=" + symptom9 +
                ", symptom10=" + symptom10 +
                '}';
    }
}
