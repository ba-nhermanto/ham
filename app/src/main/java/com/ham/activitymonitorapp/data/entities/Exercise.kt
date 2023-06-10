package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import java.sql.Timestamp

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "weight") val weight: Int,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "startTime") val startTime: Timestamp,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "averageHrBpm") val averageHrBpm: Int?,
    @ColumnInfo(name = "maxHrBpm") val maxHrBpm: Int?,
    @ColumnInfo(name = "minHrBpm") val minHrBpm: Int?,
    @ColumnInfo(name = "caloriesBurned") val caloriesBurned: Int?,
    @ColumnInfo(name = "done") val done: Boolean,
)
