package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.UserRepository
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

    val bufferUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
    companion object {
        const val TAG = "UserViewModel"
    }
    suspend fun setActiveUser(userId: Long) {
        try {
            activeUser.value = userRepository.setActiveUser(userId)
            showToast("User $userId is active")
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    suspend fun upsertUser(user: User) {
        try {
            val upserted = userRepository.upsertUser(user)
            if (upserted != null) {
                setActiveUser(upserted.userId)
                bufferUser.value = upserted
                showToast("User ${upserted.userId} is saved")
            } else {
                Log.e(TAG, "failed to upsert user $user")
            }
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

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}