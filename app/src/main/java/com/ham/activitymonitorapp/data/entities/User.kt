package com.ham.activitymonitorapp.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import com.ham.activitymonitorapp.other.Constants.MAX_BPM_GLOBAL
import java.sql.Date
import java.time.LocalDate
import java.time.Period

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "weight") var weight: Int,
    @TypeConverters(DateTypeConverter::class)
    @ColumnInfo(name = "dateOfBirth") val dateOfBirth: Date,
    @ColumnInfo(name = "gender") var gender: Gender,
    @ColumnInfo(name = "deviceId") var deviceId: String,
    @ColumnInfo(name = "active") var active: Boolean = false
) {
    fun getAge(): Int {
        val currentDate = LocalDate.now()
        val dob = LocalDate.parse(dateOfBirth.toString())
        return Period.between(dob, currentDate).years
    }

    fun getMaxBpm(): Int {
        return (MAX_BPM_GLOBAL - 0.64*getAge()).toInt()
    }
}
