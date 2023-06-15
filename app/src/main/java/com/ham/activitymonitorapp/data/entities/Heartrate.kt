package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.TimestampTypeConverter
import java.sql.Timestamp

@Entity(tableName = "heartrates")
data class Heartrate(
    @PrimaryKey(autoGenerate = true) val hrId: Int = 0,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "bpm") var bpm: Int,
    @TypeConverters(TimestampTypeConverter::class)
    @ColumnInfo(name = "timestamp") val timestamp: Timestamp,
)
