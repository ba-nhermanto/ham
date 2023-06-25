package com.ham.activitymonitorapp.repositories

import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import com.ham.activitymonitorapp.exceptions.ExerciseNotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.sql.Timestamp

@RunWith(JUnit4::class)
class ExerciseRepositoryTest {

    @Mock
    private lateinit var exerciseDao: ExerciseDao

    private lateinit var exerciseRepository: ExerciseRepository

    private val USER_ID = 1L
    private val EXERCISE_ID = 1L

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        exerciseRepository = ExerciseRepository(exerciseDao)
    }

    @Test
    fun `test getAllExercises`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.loadAllByIds(LongArray(EXERCISE_ID.toInt()))).thenReturn(listOf(exercise))

        // Act
        val result = exerciseRepository.getExercisesById(LongArray(EXERCISE_ID.toInt()))

        // Assert
        assertEquals(listOf(exercise), result)
        verify(exerciseDao).loadAllByIds(LongArray(EXERCISE_ID.toInt()))
    }

    @Test
    fun `test getExercises`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getAll()).thenReturn(listOf(exercise))

        // Act
        val result = exerciseRepository.getExercises()

        // Assert
        assertEquals(listOf(exercise), result)
        verify(exerciseDao).getAll()
    }

    @Test
    fun `test createExercise`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.insert(exercise)).thenReturn(exercise.exerciseId)
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        exerciseRepository.upsertExercise(exercise)

        // Assert
        verify(exerciseDao).insert(exercise)
    }

    @Test
    fun `test updateExercise`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.insert(exercise)).thenReturn(exercise.exerciseId)
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        exerciseRepository.upsertExercise(exercise)

        // Assert
        verify(exerciseDao).insert(exercise)
    }

    @Test
    fun `test upsert`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.insert(exercise)).thenReturn(exercise.exerciseId)
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        exerciseRepository.upsertExercise(exercise)

        // Assert
        verify(exerciseDao).insert(exercise)
    }

    @Test
    fun `test delete exercise`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(null)

        try {
            // Act
            exerciseRepository.deleteExercise(exercise)
        } catch (e: ExerciseNotFoundException) {
            // Assert
            verify(exerciseDao, never()).delete(exercise)
        }
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
}

