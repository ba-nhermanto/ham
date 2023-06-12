package com.ham.activitymonitorapp.repositories

import com.ham.activitymonitorapp.data.dao.ExerciseDao
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import com.ham.activitymonitorapp.exception.ExerciseNotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
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

    private val USER_ID = 1
    private val EXERCISE_ID = 1

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        exerciseRepository = ExerciseRepository(exerciseDao)
    }

    @Test
    fun `test setActiveExercise`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        val result = exerciseRepository.setActiveExercise(EXERCISE_ID)

        // Assert
        assertEquals(exercise, result)
        assertEquals(exercise, exerciseRepository.getActiveExercise())
        verify(exerciseDao).getById(USER_ID)
    }

    @Test
    fun `test getAllExercises`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.loadAllByIds(IntArray(EXERCISE_ID))).thenReturn(listOf(exercise))

        // Act
        val result = exerciseRepository.getExercisesById(IntArray(EXERCISE_ID))

        // Assert
        assertEquals(listOf(exercise), result)
        verify(exerciseDao).loadAllByIds(IntArray(EXERCISE_ID))
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

        // Act
        exerciseRepository.createExercise(exercise)

        // Assert
        verify(exerciseDao).insertAll(exercise)
    }

    @Test
    fun `test updateExercise`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        exerciseRepository.updateExercise(exercise)

        // Assert
        verify(exerciseDao).updateExercises(exercise)
    }

    @Test
    fun `test createOrUpdateExercise when exercise does not exist`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(exercise.userId)).thenReturn(null)

        // Act
        exerciseRepository.createOrUpdateExercise(exercise)

        // Assert
        verify(exerciseDao).insertAll(exercise)
    }

    @Test
    fun `test createOrUpdateUser when user exists`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(exercise)

        // Act
        exerciseRepository.createOrUpdateExercise(exercise)

        // Assert
        verify(exerciseDao).updateExercises(exercise)
    }

    @Test
    fun `test delete exercise not found`() = runTest {
        // Setup
        val exercise = supplyExercise()
        `when`(exerciseDao.getById(EXERCISE_ID)).thenReturn(null)

        try {
            // Act
            exerciseRepository.deleteExercise(exercise)
            fail("Expected ExerciseNotFoundException to be thrown")
        } catch (e: ExerciseNotFoundException) {
            // Assert
            assertEquals("Exercise with exerciseId $EXERCISE_ID not found", e.message)
            verify(exerciseDao, never()).delete(exercise)
        }
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
            weight = 80,
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

