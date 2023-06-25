package com.ham.activitymonitorapp.data.repositories

import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.exceptions.ExerciseNotFoundException
import com.ham.activitymonitorapp.exceptions.NoActiveExerciseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {

    suspend fun getActiveExercise(): Exercise = withContext(Dispatchers.IO) {
        exerciseDao.getActiveExercise() ?: throw NoActiveExerciseException()
    }

    suspend fun getExercises(): List<Exercise> = withContext(Dispatchers.IO) {
        exerciseDao.getAll()
    }

    suspend fun setExerciseDoneById(id: Long): Exercise = withContext(Dispatchers.IO) {
        val exercise = exerciseDao.getById(id)
        if (exercise != null) {
            exercise.done = true
            upsertExercise(exercise)
        } else {
            throw ExerciseNotFoundException(id)
        }
    }

    suspend fun getExerciseById(id: Long): Exercise = withContext(Dispatchers.IO) {
        exerciseDao.getById(id) ?: throw ExerciseNotFoundException(id)
    }

    suspend fun getExercisesById(ids: LongArray): List<Exercise> = withContext(Dispatchers.IO) {
        exerciseDao.loadAllByIds(ids)
    }

    suspend fun deleteExercise(exercise: Exercise) = withContext(Dispatchers.IO) {
        exerciseDao.delete(exercise)
    }

    suspend fun upsertExercise(exercise: Exercise): Exercise = withContext(Dispatchers.IO) {
        val id = exerciseDao.insert(exercise)
        getExerciseById(id)
    }
}