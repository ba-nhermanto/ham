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
import com.ham.activitymonitorapp.exceptions.NoActiveUserException
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

    var activeUser: User? = null

    val currentExercise: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }

    val currentExercisesList: MutableLiveData<List<Exercise>> by lazy {
        MutableLiveData<List<Exercise>>()
    }

    companion object {
        const val TAG = "EXERCISE_VIEW_MODEL"
    }

    init {
        subscribeToExerciseEvent()
        subscribeToActiveUserEvent()

        viewModelScope.launch {
            Log.d(TAG, "getting active user and related exercises")
            setActiveUser()
            initExerciseList()
        }
    }

    private fun subscribeToActiveUserEvent() {
        ActiveUserEventBus.subscribe { event ->
            Log.d(TAG, "user change event received: ${event.user?.userId}")

            if (event.user == null) {
                currentExercisesList.value = listOf()
            } else {
                getExerciseList(event.user.userId)
            }

            activeUser = event.user
            currentExercise.value = null
        }
    }

    private fun subscribeToExerciseEvent() {
        ExerciseEventBus.subscribe { event ->
            onExerciseReceived(event.exercise)
        }
    }

    private fun onExerciseReceived(exercise: Exercise) {
        viewModelScope.launch {
            currentExercise.value = exercise
            activeUser?.let { getExerciseList(it.userId) }
        }
    }

    fun createExerciseWithBasicFields(): Exercise {
        if (activeUser == null) {
            throw NoActiveUserException()
        }

        val exercise = Exercise(
            duration = 0,
            userId = activeUser!!.userId,
            averageHrBpm = 0,
            startTime = Timestamp(System.currentTimeMillis()),
            maxHrBpm = 0,
            minHrBpm = 0,
            caloriesBurned = 0,
            done = false
        )
        currentExercise.value = exercise

        viewModelScope.launch {
            exerciseRepository.upsertExercise(exercise)
        }

        return exercise
    }

    private fun getExerciseList(id: Long) {
        viewModelScope.launch {
            Log.d(TAG, "getting list of exercises")
            currentExercisesList.value = userRepository.getListOfDoneExercisesByUserId(id)
        }
    }

    fun initExerciseList() {
        activeUser?.let { getExerciseList(it.userId) }
    }

    suspend fun setActiveUser() {
        activeUser = userRepository.getActiveUser()
        Log.d(TAG, "found user: ${activeUser?.userId}")
    }

}