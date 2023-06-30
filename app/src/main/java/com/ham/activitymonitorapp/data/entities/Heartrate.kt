package com.ham.activitymonitorapp.data.entities

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE
import com.ham.activitymonitorapp.data.typeconverters.TimestampTypeConverter
import java.sql.Timestamp

@Entity(tableName = "heartrates", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = CASCADE
    )
])
data class Heartrate(
    @PrimaryKey(autoGenerate = true) val hrId: Long = 0,
    @ColumnInfo(name = "userId", index = true) val userId: Long,
    @ColumnInfo(name = "bpm") var bpm: Int,
    @TypeConverters(TimestampTypeConverter::class)
    @ColumnInfo(name = "timestamp") val timestamp: Timestamp,
)
