package com.ham.activitymonitorapp.services

import com.ham.activitymonitorapp.data.entities.Activity
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.events.ActivityEvent
import com.ham.activitymonitorapp.events.ActivityEventBus
import com.ham.activitymonitorapp.events.HeartrateEventBus
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import kotlinx.coroutines.runBlocking

class ActivityService(private val userViewModel: UserViewModel) {
    private var activeUser: User = runBlocking {
        userViewModel.getActiveUser()
    }

    init {
        subscribeToHeartRateEvent()
    }

    private fun subscribeToHeartRateEvent() {
        HeartrateEventBus.subscribe { hrEvent ->
            activeUser = runBlocking {
                userViewModel.getActiveUser()
            }

            updateActivity(hrEvent.bpm, user = activeUser)
        }
    }

    private fun updateActivity(bpm: Int, user: User) {
        val newActivity = activityClassificator(bpm, user)
        ActivityEventBus.publish(ActivityEvent(newActivity))
    }

    private fun activityClassificator(bpm: Int, user: User) : Activity {
        val maxBpm = user.getMaxBpm()
        val veryLightBpm = maxBpm * Activity.VERY_LIGHT.intensity
        val lightBpm = maxBpm * Activity.LIGHT.intensity
        val moderateBpm = maxBpm * Activity.MODERATE.intensity
        val hardBpm = maxBpm * Activity.HARD.intensity
        val veryHardBpm = maxBpm * Activity.VERY_HARD.intensity

        return when (bpm) {
            in 0 until veryLightBpm.toInt() -> Activity.VERY_LIGHT
            in veryLightBpm.toInt() until lightBpm.toInt() -> Activity.LIGHT
            in lightBpm.toInt() until moderateBpm.toInt() -> Activity.MODERATE
            in moderateBpm.toInt() until hardBpm.toInt() -> Activity.HARD
            in hardBpm.toInt() until veryHardBpm.toInt() -> Activity.VERY_HARD
            else -> Activity.VERY_HARD
        }

    }

}