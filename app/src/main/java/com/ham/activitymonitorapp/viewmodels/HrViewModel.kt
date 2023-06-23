package com.ham.activitymonitorapp.viewmodels

import android.app.Application
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class HrViewModel @Inject constructor(
    private val hrRepository: HeartrateRepository,
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var activeUser: User

    val currentHrBpm: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentActivity: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val currentBatteryStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    init {
        subscribeToHeartRateEvent()
        subscribeToBatteryEvent()
        subscribeToActivityEvent()
        subscribeToActiveUserEvent()
        viewModelScope.launch {
            activeUser =  getActiveUser()
        }

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
            activeUser = event.user
        }
    }


    private fun onBatteryEventReceived(batteryEvent: Boolean) {
        currentBatteryStatus.value = batteryEvent
    }

    private fun onHrReceived(newHrBpm: Int) {
        currentHrBpm.value = newHrBpm
        val hr = Heartrate(
            userId = activeUser.userId,
            bpm = newHrBpm,
            timestamp = Timestamp(System.currentTimeMillis())
        )
        runBlocking {
            hrRepository.save(hr)
        }
    }

    private fun onActivityReceived(newActivity: Activity) {
        currentActivity.value = newActivity.type
    }

    suspend fun getActiveUser(): User {
        return userRepository.getActiveUser()
    }

    suspend fun getUserListOfHrBpmByUserId(id: Long): List<Int> {
        return userRepository.getListOfHrBpmByUserId(id)
    }

    override fun onCleared() {
        HeartrateEventBus.unsubscribe {
            currentHrBpm.value = null
        }
        BatteryEventBus.unsubscribe {

        }
        super.onCleared()
    }
}