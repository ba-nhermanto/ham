package com.ham.activitymonitorapp.data.typeconverters

import androidx.room.TypeConverter
import java.sql.Date

class DateTypeConverter {
    @TypeConverter
    fun fromDate(value: Long?): Date? {
        return if (value != null) Date(value) else null
    }

    @TypeConverter
    fun toDate(date: Date?): Long? {
        return date?.time
    }
}