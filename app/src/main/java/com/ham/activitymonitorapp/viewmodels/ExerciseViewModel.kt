package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import com.ham.activitymonitorapp.data.repositories.UserRepository
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.events.ExerciseEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var activeUser: User

    val currentExercise: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }

    companion object {
        const val TAG = "EXERCISE_VIEW_MODEL"
    }

    init {
        subscribeToExerciseEvent()
        subscribeToActiveUserEvent()
        viewModelScope.launch {
            activeUser =  getActiveUser()
        }
    }

    private fun subscribeToActiveUserEvent() {
        ActiveUserEventBus.subscribe { event ->
            activeUser = event.user
        }
    }

    private fun subscribeToExerciseEvent() {
        ExerciseEventBus.subscribe { event ->
            onExerciseReceived(event.exercise)
        }
    }

    private fun onExerciseReceived(exercise: Exercise) {
        viewModelScope.launch {
            val ex = saveExercise(exercise)
            currentExercise.value = ex
            Log.d(TAG, "Exercise is saved: $ex")
        }
    }

    fun createExerciseWithBasicFields(): Exercise {
        val exercise = Exercise(
            duration = 0,
            userId = activeUser.userId,
            averageHrBpm = 0,
            startTime = Timestamp(System.currentTimeMillis()),
            maxHrBpm = 0,
            minHrBpm = 0,
            caloriesBurned = 0,
            done = false
        )
        currentExercise.value = exercise

        viewModelScope.launch {
            saveExercise(exercise)
        }

        return exercise
    }

    suspend fun getActiveUser(): User {
        return userRepository.getActiveUser()
    }

    suspend fun saveExercise(exercise: Exercise): Exercise {
        return exerciseRepository.upsertExercise(exercise)
    }

//    fun validateActiveExercise(): Exercise {
//        runBlocking {
//            val exercise = exerciseRepository.getActiveExercise()
//        }
//    }

}