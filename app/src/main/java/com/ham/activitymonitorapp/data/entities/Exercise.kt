package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import java.sql.Timestamp

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    @ColumnInfo(name = "userId") val userId: Int,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "startTime") val startTime: Timestamp,
    @ColumnInfo(name = "duration") var duration: Int,
    @ColumnInfo(name = "averageHrBpm") var averageHrBpm: Int?,
    @ColumnInfo(name = "maxHrBpm") var maxHrBpm: Int?,
    @ColumnInfo(name = "minHrBpm") var minHrBpm: Int?,
    @ColumnInfo(name = "caloriesBurned") var caloriesBurned: Int?,
    @ColumnInfo(name = "done") var done: Boolean,
)
