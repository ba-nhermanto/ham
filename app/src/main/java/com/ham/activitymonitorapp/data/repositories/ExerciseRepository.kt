package com.ham.activitymonitorapp.data.repositories

import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.entities.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    private lateinit var activeExercise: Exercise

    fun getActiveExercise(): Exercise {
        return activeExercise
    }

    suspend fun getExercises(): List<Exercise> = withContext(Dispatchers.IO) {
        exerciseDao.getAll()
    }

    suspend fun setActiveExercise(id: Int): Exercise = withContext(Dispatchers.IO) {
        activeExercise = exerciseDao.getById(id)!!
        activeExercise
    }

    suspend fun getExerciseById(id: Int): Exercise? = withContext(Dispatchers.IO) {
        exerciseDao.getById(id)
    }

    suspend fun deleteExerciseById(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.delete(exercise)
    }

    private suspend fun createExercise(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.insertAll(exercise)
    }

    private suspend fun updateexercise(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.updateExercises(exercise)
    }

    suspend fun createOrUpdateexercise(exercise: Exercise) = withContext(Dispatchers.IO) {
        val existingexercise = getExerciseById(exercise.exerciseId)

        if (existingexercise == null) {
            // exercise does not exist, create a new exercise
            createExercise(exercise)
        } else {
            // exercise already exists, update the existing exercise
            updateexercise(exercise)
        }
    }
}