package com.ham.activitymonitorapp.data.dao

import androidx.room.*
import com.ham.activitymonitorapp.data.entities.Heartrate

@Dao
interface HeartrateDao {
    @Query("SELECT * FROM heartrates")
    suspend fun getAll(): List<Heartrate>

    @Query("SELECT * FROM heartrates WHERE hrId IN (:id)")
    suspend fun getById(id: Long): Heartrate?

    @Query("SELECT * FROM heartrates WHERE hrId IN (:hrIds)")
    suspend fun loadAllByIds(hrIds: LongArray): List<Heartrate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg heartrates: Heartrate)

    @Update
    suspend fun update(vararg heartrates: Heartrate)

    @Delete
    suspend fun delete(heartrate: Heartrate)
}