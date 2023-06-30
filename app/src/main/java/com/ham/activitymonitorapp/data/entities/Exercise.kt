package com.ham.activitymonitorapp.data.entities

import androidx.room.*
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import java.sql.Timestamp

@Entity(tableName = "exercises", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Long = 0,
    @ColumnInfo(name = "userId", index = true) val userId: Long,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "startTime") val startTime: Timestamp,
    @ColumnInfo(name = "duration") var duration: Int,
    @ColumnInfo(name = "averageHrBpm") var averageHrBpm: Int?,
    @ColumnInfo(name = "maxHrBpm") var maxHrBpm: Int?,
    @ColumnInfo(name = "minHrBpm") var minHrBpm: Int?,
    @ColumnInfo(name = "caloriesBurned") var caloriesBurned: Int?,
    @ColumnInfo(name = "done") var done: Boolean,
)
