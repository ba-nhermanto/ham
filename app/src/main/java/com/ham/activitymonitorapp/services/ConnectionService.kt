package com.ham.activitymonitorapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ham.activitymonitorapp.events.BatteryEvent
import com.ham.activitymonitorapp.events.BatteryEventBus
import com.ham.activitymonitorapp.events.HeartrateEvent
import com.ham.activitymonitorapp.events.HeartrateEventBus
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrBroadcastData
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

@AndroidEntryPoint
class ConnectionService : Service() {

    private val polarBleApi: PolarBleApi by lazy {
        PolarBleApiDefaultImpl.defaultImplementation(
            appContext,
            setOf(
                PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
                PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_DEVICE_TIME_SETUP,
                PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
            )
        )
    }

    private var bluetoothEnabled: Boolean = false

    private var deviceConnected: Boolean = false

    private lateinit var appContext: Context

    private lateinit var broadcastDisposable: Disposable

    private lateinit var deviceId: String

    private var autoConnectDisposable: Disposable? = null

    private val binder = ConnectionServiceBinder()

    inner class ConnectionServiceBinder : Binder() {
        fun getService(): ConnectionService = this@ConnectionService
    }

    override fun onBind(intent: Intent): IBinder {
        deviceId = intent.getStringExtra("deviceId").toString()

        return binder
    }

    companion object {
        private const val SERVICE_ID = 875642
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        const val TAG = "ConnectionService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(SERVICE_ID, notification())

        appContext = baseContext
        connectPolar(deviceId)
        setPolarApiCallback()
        listenHrBroadcast()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun connectPolar(polarId: String) {
        polarBleApi.run {
            setApiLogger { s: String? -> Log.d(TAG, "CONNECTION_SERVICE LOGGER $s") }

            deviceConnected = try {
                connectToDevice(polarId)
                true
            } catch (polarInvalidArgument: PolarInvalidArgument) {
                Log.e(TAG, "Failed to connect. Reason $polarInvalidArgument ")
                false
            }

            setAutomaticReconnection(true)
        }
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
            .setContentTitle("Heart Rate Service")
            .setContentText("Monitoring heart rate")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun setPolarApiCallback() {
        polarBleApi.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(powered: Boolean) {
                Log.d(TAG, "BLE power: $powered")
                bluetoothEnabled = powered
                if (powered) {
                    showToast("Bluetooth on")
                } else {
                    showToast("Bluetooth off")
                }
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                deviceConnected = true
                showToast("device connected: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                deviceConnected = false
                showToast("device disconnected: ${polarDeviceInfo.deviceId}")
            }

            override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                Log.d(TAG, "DIS INFO uuid: $uuid value: $value")
            }

            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d(TAG, "BATTERY LEVEL: $level")
                if (level <= 10) {
                    showToast("Battery is low: $level")
                }
            }
        })
    }

    fun autoConnect() {
        autoConnectDisposable = polarBleApi.autoConnectToDevice(-60, "180D", null)
            .subscribe(
                { Log.d(TAG, "auto connect search complete") },
                { throwable: Throwable -> Log.e(TAG, "" + throwable.toString()) }
            )
    }

    fun connectOrDisconnect() {
        try {
            if (deviceConnected) {
                polarBleApi.disconnectFromDevice(deviceId)
                deviceConnected = false
            } else {
                polarBleApi.connectToDevice(deviceId)
            }
        } catch (polarInvalidArgument: PolarInvalidArgument) {
            val attempt = if (deviceConnected) {
                "disconnect"
            } else {
                "connect"
            }
            Log.e(TAG, "Failed to $attempt. Reason $polarInvalidArgument ")
        }
    }

    private fun listenHrBroadcast() {
        if (!this::broadcastDisposable.isInitialized || broadcastDisposable.isDisposed) {
            broadcastDisposable = polarBleApi.startListenForPolarHrBroadcasts(null)
                .subscribe(
                    { polarBroadcastData: PolarHrBroadcastData ->
                        Log.d(TAG, "HR BROADCAST ${polarBroadcastData.polarDeviceInfo.deviceId} HR: ${polarBroadcastData.hr} batt: ${polarBroadcastData.batteryStatus}")
                        onHrReceived(polarBroadcastData.hr)
                        onBatteryReceived(polarBroadcastData.batteryStatus)
                    },
                    { error: Throwable ->
                        Log.e(TAG, "Broadcast listener failed. Reason $error")
                    },
                    { Log.d(TAG, "complete") }
                )
        } else {
            broadcastDisposable.dispose()
        }
    }

    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(
                appContext, message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onHrReceived(hrBpm: Int) {
        val event = HeartrateEvent(hrBpm)
        HeartrateEventBus.publish(event)
    }

    private fun onBatteryReceived(health: Boolean) {
        val event = BatteryEvent(health)
        BatteryEventBus.publish(event)
    }

    override fun onDestroy() {
        super.onDestroy()

        polarBleApi.shutDown()
        stopSelf()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        polarBleApi.shutDown()
        return super.onUnbind(intent)
    }

    fun isDeviceConnected(): Boolean {
        return deviceConnected
    }

    fun getDeviceId(): String {
        return deviceId
    }
}