package com.ham.activitymonitorapp.data.typeconverters

import androidx.room.TypeConverter
import java.sql.Timestamp

class TimestampTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return if (value != null) Timestamp(value) else null
    }

    @TypeConverter
    fun toTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }
}