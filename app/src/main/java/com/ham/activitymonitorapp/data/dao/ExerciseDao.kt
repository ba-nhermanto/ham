package com.ham.activitymonitorapp.data.dao

import androidx.room.*
import com.ham.activitymonitorapp.data.entities.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE exerciseId IN (:id)")
    suspend fun getById(id: Long): Exercise?

    @Query("SELECT * FROM exercises WHERE exerciseId IN (:ids)")
    suspend fun loadAllByIds(ids: LongArray): List<Exercise>

    @Query("SELECT * FROM exercises WHERE done = 0")
    suspend fun getActiveExercise(): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Transaction
    suspend fun insert(exercise: Exercise): Long

    @Delete
    suspend fun delete(exercise: Exercise)
}