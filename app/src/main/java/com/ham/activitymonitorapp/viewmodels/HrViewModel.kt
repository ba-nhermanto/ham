package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ham.activitymonitorapp.data.entities.Activity
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.HeartrateRepository
import com.ham.activitymonitorapp.data.repositories.UserRepository
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.events.ActivityEventBus
import com.ham.activitymonitorapp.events.BatteryEventBus
import com.ham.activitymonitorapp.events.HeartrateEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class HrViewModel @Inject constructor(
    private val hrRepository: HeartrateRepository,
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    var activeUser: User? = null

    val currentHrBpm: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentActivity: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentBatteryStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val currentHrList: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    companion object {
        const val TAG = "HR_VIEW_MODEL"
    }


    init {
        subscribeToHeartRateEvent()
        subscribeToBatteryEvent()
        subscribeToActivityEvent()
        subscribeToActiveUserEvent()
        initActiveUserAndListHr()
    }

    private fun subscribeToHeartRateEvent() {
        HeartrateEventBus.subscribe { hrEvent ->
            onHrReceived(hrEvent.bpm)
        }
    }

    private fun subscribeToBatteryEvent() {
        BatteryEventBus.subscribe { event ->
            onBatteryEventReceived(event.health)
        }
    }

    private fun subscribeToActivityEvent() {
        ActivityEventBus.subscribe { activityEvent ->
            onActivityReceived(activityEvent.activity)
        }
    }

    private fun subscribeToActiveUserEvent() {
        ActiveUserEventBus.subscribe { event ->
            Log.d(TAG, "user changed: ${event.user?.userId}")
            activeUser = event.user
            viewModelScope.launch {
                currentHrList.value = withContext(Dispatchers.IO) {
                    activeUser?.let { getListOfHrBpmByUserId(it.userId) }
                }
            }
        }
    }


    private fun onBatteryEventReceived(batteryEvent: Boolean) {
        currentBatteryStatus.value = batteryEvent
    }

    private fun onHrReceived(newHrBpm: Int) {
        Log.d(TAG, "new hr data received = $newHrBpm")
        currentHrBpm.value = newHrBpm
        val hr = Heartrate(
            userId = activeUser!!.userId,
            bpm = newHrBpm,
            timestamp = Timestamp(System.currentTimeMillis())
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hrRepository.save(hr)
            }
        }

        val temp = currentHrList.value?.toMutableList()
        temp?.add(newHrBpm)
        currentHrList.value = temp?.toList()
        Log.d(TAG, "hr list modified = ${currentHrList.value}")
    }

    private fun onActivityReceived(newActivity: Activity) {
        currentActivity.value = newActivity.type
    }

    suspend fun getActiveUser(): User {
        return userRepository.getActiveUser()
    }

    suspend fun getListOfHrBpmByUserId(id: Long): List<Int> {
        return userRepository.getListOfHrBpmByUserId(id)
    }

    fun initActiveUserAndListHr() {
        runBlocking {
            activeUser =  getActiveUser()
            Log.d(TAG, "active user: ${activeUser?.userId}")
            activeUser?.let {
                currentHrList.value = getListOfHrBpmByUserId(activeUser!!.userId)
            }
        }
    }

    override fun onCleared() {
        HeartrateEventBus.unsubscribe {
            currentHrBpm.value = null
        }
        BatteryEventBus.unsubscribe {
        }
        ActiveUserEventBus.unsubscribe {
        }
        ActivityEventBus.unsubscribe {
        }
        super.onCleared()
    }
}