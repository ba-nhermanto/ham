package com.ham.activitymonitorapp.services

import android.app.ActivityManager
import android.content.Context

class ServiceRunningChecker {

    @SuppressWarnings("deprecation")
    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

        for (service in runningServices) {
            if (service.service.className == serviceClass.name) {
                return true
            }
        }

        return false
    }
}