package com.ham.activitymonitorapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.dao.HeartrateDao
import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.typeconverters.DateTypeConverter
import com.ham.activitymonitorapp.data.typeconverters.TimestampTypeConverter
import com.ham.activitymonitorapp.other.Constants.DATABASE_NAME


@Database(entities = [User::class, Exercise::class, Heartrate::class], version = 3)
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
                )
                    .addMigrations(MIGRATION_2_3)
                    .build().also {
                    INSTANCE = it
                }
            }
        }

        // Migration from version 2 to 3
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform the necessary SQL query to add the new field
                database.execSQL("ALTER TABLE users ADD COLUMN active INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}