package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.UserRepository
import com.ham.activitymonitorapp.events.ActiveUserChangeEvent
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.view.Toaster
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application,
) : AndroidViewModel(application) {

    val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }

    val activeUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }

    private val toaster = Toaster()

    companion object {
        const val TAG = "UserViewModel"
    }

    init {
        viewModelScope.launch {
            if (!activeUser.isInitialized) {
                Log.d(TAG, "getting active user")
                activeUser.value = getActiveUser()
            }

            Log.d(TAG, "active user: ${activeUser.value}")
        }
    }

    suspend fun setActiveUser(userId: Long) {
        try {
            val au = userRepository.setActiveUser(userId)
            activeUser.value = au
            publishActiveUser(au)
            toaster.showToast("User ${au.userId} is active", getApplication())
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    suspend fun upsertUser(user: User) {
        try {
            val upserted = userRepository.upsertUser(user)
            setActiveUser(upserted.userId)
            toaster.showToast("User ${upserted.userId} is saved", getApplication())
        }catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }

    }

    fun getUsers() {
        viewModelScope.launch {
            users.value = userRepository.getUsers()
        }
    }

    suspend fun getActiveUser(): User {
        return userRepository.getActiveUser()
    }

    suspend fun deleteActiveUser() {
        var deleted = false
        activeUser.value?.let {
            try {
                deleted = deleteUser(it)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                toaster.showToast("please stop running exercise", getApplication())
            }

            if (deleted) {
                try {
                    setActiveUser(userRepository.getUsers()[0].userId)
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    Log.d(TAG, "no users in the database")
                    activeUser.value = null
                    publishActiveUser(null)
                }
            }
        }
    }

    private suspend fun deleteUser(user: User): Boolean {
        val activeExercise = userRepository.getActiveExerciseByUserId(user.userId)
        if (activeExercise.isEmpty()) {
            userRepository.deleteUser(user)
            return true
        } else {
            throw Exception("user: ${user.userId} has active exercise: $activeExercise")
        }
    }

    private fun publishActiveUser(user: User?) {
        ActiveUserEventBus.publish(
            ActiveUserChangeEvent(user)
        )
    }
}