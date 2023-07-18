package com.ham.activitymonitorapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import com.ham.activitymonitorapp.data.repositories.UserRepository
import com.ham.activitymonitorapp.events.ExerciseEvent
import com.ham.activitymonitorapp.events.ExerciseEventBus
import com.ham.activitymonitorapp.events.HeartrateEvent
import com.ham.activitymonitorapp.events.HeartrateEventBus
import com.ham.activitymonitorapp.exceptions.NoActiveExerciseException
import com.ham.activitymonitorapp.exceptions.UserNotFoundException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Timestamp
import javax.inject.Inject

@AndroidEntryPoint
class ExerciseService: Service() {

    @Inject lateinit var exerciseRepository: ExerciseRepository

    @Inject lateinit var userRepository: UserRepository

    private val binder = ExerciseServiceBinder()

    private var listOfHr = mutableListOf<Int>()

    private lateinit var user: User

    private lateinit var activeExercise: Exercise

    private val hrListener: (HeartrateEvent) -> Unit = { event ->
        onHrReceived(event.bpm)
    }

    inner class ExerciseServiceBinder : Binder() {
        fun getService(): ExerciseService = this@ExerciseService
    }

    companion object {
        const val TAG = "EXERCISE_SERVICE"
        const val SERVICE_ID = 2
        private const val CHANNEL_ID = "2"
        private const val CHANNEL_NAME = "channel_exercise_service"
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(SERVICE_ID, notification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startExercise()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Stopping exercise")
        stopExercise()
        listOfHr.clear()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "unbinding service exercise")
        return super.onUnbind(intent)
    }

    private fun notification(): Notification {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Exercise Service")
            .setContentText("Monitoring exercise")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun startExercise() {
        runBlocking {
            try {
                activeExercise = exerciseRepository.getActiveExercise()
                setUser(activeExercise.userId)
            } catch (e: NoActiveExerciseException) {
                Log.e(TAG, e.message.toString())
                showToast(e.message.toString())
            }

        }
        subscribeToHeartRateEvent()

        Log.d(TAG, "Exercise started: $activeExercise")
    }


    private fun stopExercise() {
        activeExercise.done = true
        HeartrateEventBus.unsubscribe(hrListener)
        processExercise()
        saveActiveExercise()
        publishExerciseEvent(activeExercise)

        Log.d(TAG, "exercise stopped: $activeExercise")
    }

    private fun subscribeToHeartRateEvent() {
        HeartrateEventBus.subscribe(hrListener)
    }

    private fun onHrReceived(bpm: Int) {
        listOfHr.add(bpm)
        processExercise()
        publishExerciseEvent(activeExercise)
    }

    private fun publishExerciseEvent(exercise: Exercise) {
        val event = ExerciseEvent(exercise)
        ExerciseEventBus.publish(event)
    }

    private fun processExercise() {
        activeExercise.averageHrBpm = listOfHr.average().toInt()
        activeExercise.maxHrBpm = listOfHr.maxOrNull()
        activeExercise.minHrBpm = listOfHr.minOrNull()
        activeExercise.duration = getSecondsBetweenTimestampAndNow(activeExercise.startTime)
        activeExercise.caloriesBurned = calculateCalories(activeExercise).toInt().coerceAtLeast(0)

        Log.d(TAG, "updating exercise: $activeExercise")
    }

    private fun getSecondsBetweenTimestampAndNow(timestamp: Timestamp): Int {
        val timestampMillis = timestamp.time
        val currentMillis = System.currentTimeMillis()
        val timeDifferenceInMillis = currentMillis - timestampMillis
        return (timeDifferenceInMillis / 1000).toInt()
    }

    private fun calculateCalories(exercise: Exercise): Float {
        return if (user.gender == Gender.MALE) {
            (exercise.duration * (0.6309*exercise.averageHrBpm!! + 0.1988*user.weight + 0.2017*user.getAge() - 55.0969) / 4.184).toFloat()
        } else {
            (exercise.duration * (0.4472*exercise.averageHrBpm!! + 0.1236*user.weight + 0.074*user.getAge() - 20.422) / 4.184).toFloat()
        }
    }

    private fun setUser(id: Long) {
        try {
            user = runBlocking {
                userRepository.getUserById(id)
            }
        } catch (e: UserNotFoundException) {
            Log.d(TAG, e.message.toString())
            showToast(e.message.toString())
        }
    }

    private fun saveActiveExercise(){
        CoroutineScope(Dispatchers.IO).launch {
            exerciseRepository.upsertExercise(activeExercise)
        }
        Log.d(TAG, "Exercise: ${activeExercise.exerciseId} is saved")
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

}