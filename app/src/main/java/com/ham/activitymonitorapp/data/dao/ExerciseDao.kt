package com.ham.activitymonitorapp.data.dao

import androidx.room.*
import com.ham.activitymonitorapp.data.entities.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE exerciseId IN (:id)")
    suspend fun getById(id: Int): Exercise?

    @Query("SELECT * FROM exercises WHERE exerciseId IN (:ids)")
    suspend fun loadAllByIds(ids: IntArray): List<Exercise>

    @Insert
    suspend fun insertAll(vararg exercises: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Update
    fun updateExercises(vararg exercise: Exercise)
}