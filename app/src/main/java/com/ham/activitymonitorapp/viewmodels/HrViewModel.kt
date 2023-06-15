package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.HeartrateRepository
import com.ham.activitymonitorapp.events.BatteryEventBus
import com.ham.activitymonitorapp.events.HeartrateEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import java.sql.Timestamp
import javax.inject.Inject

@HiltViewModel
class HrViewModel @Inject constructor(
    private val hrRepository: HeartrateRepository,
    application: Application
) : AndroidViewModel(application) {

    private lateinit var activeUser: User

    val currentHrBpm: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentBatteryStatus: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    init {
        subscribeToHeartRateEvent()
        subscribeToBatteryEvent()
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

    override fun onCleared() {
        HeartrateEventBus.unsubscribe {
            currentHrBpm.value = null
        }
        BatteryEventBus.unsubscribe {

        }
        super.onCleared()
    }
}