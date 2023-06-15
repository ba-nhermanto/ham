package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import java.sql.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "weight") var weight: Int,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "dateOfBirth") val dateOfBirth: Date,
    @ColumnInfo(name = "gender") var gender: Gender,
    @ColumnInfo(name = "deviceId") var deviceId: String,
)
