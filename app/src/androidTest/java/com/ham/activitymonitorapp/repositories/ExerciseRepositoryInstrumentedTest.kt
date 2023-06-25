package com.ham.activitymonitorapp.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.database.HamDatabase
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.sql.Timestamp

class ExerciseRepositoryInstrumentedTest {
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var database: HamDatabase

    private val USER_ID = 1L
    private val EXERCISE_ID = 1L

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HamDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        exerciseDao = database.exerciseDao()
        exerciseRepository = ExerciseRepository(exerciseDao)
    }

    @After
    fun cleanup() {
        cleanupDb()
        database.close()
    }

    private fun cleanupDb() = runBlocking {
        val exs = exerciseRepository.getExercises()
        exs.forEach { exerciseRepository.deleteExercise(it) }
    }

    @Test
    fun testGetExercises() = runBlocking {
        // Setup
        val exercises = supplyListOfExercise()

        exercises.forEach { exerciseRepository.upsertExercise(it) }

        // Act
        val result = exerciseRepository.getExercises()

        // Assert
        assertEquals(exercises, result)
    }

    @Test
    fun testGetExerciseById() = runBlocking {
        // Setup
        val exercise = supplyExercise()
        exerciseRepository.upsertExercise(exercise)

        // Act
        val result = exerciseRepository.getExerciseById(exercise.exerciseId)

        // Assert
        assertEquals(exercise, result)
    }

    @Test
    fun testGetExercisesById() = runBlocking {
        // Setup
        val exercises = supplyListOfExercise()
        exercises.forEach { exerciseRepository.upsertExercise(it) }
        val exerciseIds = exercises.map { it.exerciseId }.toLongArray()

        // Act
        val result = exerciseRepository.getExercisesById(exerciseIds)

        // Assert
        assertEquals(exercises, result)
    }

    @Test
    fun testDeleteExercise() = runBlocking {
        // Setup
        val exercise = supplyExercise()
        exerciseRepository.upsertExercise(exercise)

        // Act
        exerciseRepository.deleteExercise(exercise)

        // Assert
        val result = exerciseRepository.getExerciseById(exercise.exerciseId)
        assertNull(result)
    }

    @Test
    fun testCreateOrUpdateExerciseWhenExerciseExists() = runBlocking {
        // Setup
        val exercise = supplyExercise()
        exerciseDao.insert(exercise)

        // Update exercise name
        val updatedExercise = supplyExercise()
        updatedExercise.caloriesBurned = 1000

        // Act
        exerciseRepository.upsertExercise(updatedExercise)

        // Assert
        val result = exerciseRepository.getExerciseById(exercise.exerciseId)
        assertEquals(updatedExercise, result)
    }

    private fun supplyExercise(): Exercise {
        return Exercise(
            exerciseId = EXERCISE_ID,
            userId = USER_ID,
            startTime = Timestamp.valueOf("2023-06-12 20:45:00"),
            duration = 5,
            averageHrBpm = 120,
            maxHrBpm = 170,
            minHrBpm = 80,
            caloriesBurned = 500,
            done = true
        )
    }

    private fun supplyListOfExercise(): List<Exercise> {
        return listOf(
            supplyExercise(),
            Exercise(
                exerciseId = 2,
                userId = USER_ID,
                startTime = Timestamp.valueOf("2023-06-13 20:45:00"),
                duration = 3,
                averageHrBpm = 120,
                maxHrBpm = 150,
                minHrBpm = 80,
                caloriesBurned = 200,
                done = true
            ),
            Exercise(
                exerciseId = 3,
                userId = USER_ID,
                startTime = Timestamp.valueOf("2023-06-15 20:45:00"),
                duration = 51,
                averageHrBpm = 120,
                maxHrBpm = 150,
                minHrBpm = 70,
                caloriesBurned = 700,
                done = true
            )
        )
    }

}