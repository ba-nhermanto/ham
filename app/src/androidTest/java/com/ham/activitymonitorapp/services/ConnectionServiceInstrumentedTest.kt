package com.ham.activitymonitorapp.services

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.ServiceTestRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConnectionServiceInstrumentedTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.FOREGROUND_SERVICE
        )


    @get:Rule
    val serviceRule = ServiceTestRule()

    @Test
    fun testServiceFunctionality() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val serviceIntent = Intent(context, ConnectionService::class.java)
            .apply {
                putExtra("deviceId", "DEVICE_ID")
            }

        val binder: IBinder = serviceRule.bindService(serviceIntent)
        serviceRule.startService(serviceIntent)
        assertNotNull(binder)

        val service = (binder as ConnectionService.ConnectionServiceBinder).getService()

        assertNotNull(service)
        assertEquals(false, service.isDeviceConnected())
        assertEquals(service.getDeviceId(), "DEVICE_ID")

        serviceRule.unbindService()
    }

}