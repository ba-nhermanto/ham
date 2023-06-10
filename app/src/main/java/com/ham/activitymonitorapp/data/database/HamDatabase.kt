package com.ham.activitymonitorapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.dao.HeartrateDao
import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import com.ham.activitymonitorapp.data.typeconverters.TimestampTypeConverter
import com.ham.activitymonitorapp.other.Constants.DATABASE_NAME


@Database(entities = [User::class, Exercise::class, Heartrate::class], version = 1)
@TypeConverters(DateTypeConverter::class, TimestampTypeConverter::class)
abstract class HamDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun heartrateDao(): HeartrateDao

    companion object {
        @Volatile
        private var INSTANCE: HamDatabase? = null

        fun getInstance(context: Context): HamDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HamDatabase::class.java,
                    DATABASE_NAME
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}