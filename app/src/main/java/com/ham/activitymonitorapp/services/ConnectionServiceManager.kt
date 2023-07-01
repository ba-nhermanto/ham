package com.ham.activitymonitorapp.services

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.util.Log
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.fragments.HomeFragment

class ConnectionServiceManager {

    fun startConnectionService(context: Context, activeUser: User) {
        try {
            val serviceIntent = Intent(context, ConnectionService::class.java)

            serviceIntent.putExtra("deviceId", activeUser.deviceId)

            context.startForegroundService(serviceIntent)

            Log.d(HomeFragment.TAG, "started hr streaming")
        } catch (e: Exception) {
            Log.e(HomeFragment.TAG, e.message.toString())
        }
    }

    fun bindConnectionService(context: Context, serviceConnection: ServiceConnection) {
        val serviceIntent = Intent(context, ConnectionService::class.java)
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun stopConnectionService(context: Context, serviceConnection: ServiceConnection, connected: Boolean) {
        if (connected) {
            try {
                val serviceIntent = Intent(context, ConnectionService::class.java)
                context.unbindService(serviceConnection)
                context.stopService(serviceIntent)
                Log.d(HomeFragment.TAG, "stopped hr streaming")
            } catch (e: Exception) {
                Log.e(HomeFragment.TAG, e.message.toString())
            }
        }
    }

}