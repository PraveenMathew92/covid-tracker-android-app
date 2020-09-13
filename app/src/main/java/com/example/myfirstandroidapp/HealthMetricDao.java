package com.example.myfirstandroidapp;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface HealthMetricDao {
    @Insert(entity = HealthMetricsEntity.class)
    void insert(HealthMetricsEntity entity);
}
