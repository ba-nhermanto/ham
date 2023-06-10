package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import java.sql.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "weight") val weight: Int,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "dateOfBirth") val dateOfBirth: Date,
    @ColumnInfo(name = "gender") val gender: Gender,
    @ColumnInfo(name = "deviceId") val deviceId: String,
)
