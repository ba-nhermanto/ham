package com.ham.activitymonitorapp.fragments

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.HomeFragmentBinding
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.services.ActivityService
import com.ham.activitymonitorapp.services.ConnectionService
import com.ham.activitymonitorapp.services.ServiceRunningChecker
import com.ham.activitymonitorapp.viewmodels.HrViewModel
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.home_fragment) {
    private val userViewModel: UserViewModel by viewModels()

    private val hrViewModel: HrViewModel by viewModels()

    private var _binding: HomeFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var activityService: ActivityService

    private lateinit var connectionService: ConnectionService

    private lateinit var activeUser: User

    private val serviceRunningChecker: ServiceRunningChecker = ServiceRunningChecker()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ConnectionService.ConnectionServiceBinder
            connectionService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    companion object {
        const val TAG = "HOME_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        activityService = ActivityService(userViewModel)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getActiveUser()

        subscribeToActiveUser()

        if (::activeUser.isInitialized) {
            handleConnect()
            showUsername()
            observeAndUpdateHrTV()
            observeAndUpdateBatteryTV()
            observeAndUpdateActivityTV()
        } else {
            binding.materialSwitch.isEnabled = false
        }

    }

    private fun startConnectionService() {
        val serviceIntent = Intent(requireContext(), ConnectionService::class.java)

        if (!(::activeUser.isInitialized)) {
            getActiveUser()
        }

        serviceIntent.putExtra("deviceId", activeUser.deviceId)
        showDeviceId(activeUser.deviceId)

        requireContext().startForegroundService(serviceIntent)
    }

    private fun bindConnectionService() {
        val serviceIntent = Intent(requireContext(), ConnectionService::class.java)
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopConnectionService() {
        val serviceIntent = Intent(requireContext(), ConnectionService::class.java)
        requireContext().stopService(serviceIntent)
        requireContext().unbindService(serviceConnection)
    }

    private fun handleConnect() {
        binding.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!serviceRunningChecker.isServiceRunning(ConnectionService::class.java, requireContext())) {
                    startConnectionAndSetUI()
                } else {
                    Log.d(TAG, "Connection Service is already running")
                }
            } else {
                stopConnectionAndSetUI()
            }
        }
    }

    private fun startConnectionAndSetUI() {
        Log.d(TAG, "starting connection service")
        startConnectionService()
        bindConnectionService()
        binding.materialSwitch.isChecked = true
        binding.connectText.text = resources.getString(R.string.connected)
        binding.connectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
    }

    private fun stopConnectionAndSetUI() {
        Log.d(TAG, "stopping connection service")
        stopConnectionService()
        binding.materialSwitch.isChecked = false
        binding.connectText.text = resources.getString(R.string.disconnected)
        binding.connectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
    }

    @SuppressLint("SetTextI18n")
    private fun showDeviceId(deviceId: String) {
        binding.connectionDevice.text = "Polar $deviceId"
    }

    private fun getActiveUser() {
        activeUser = runBlocking {
            userViewModel.getActiveUser()
        }
    }

    private fun showUsername() {
        binding.username.text = activeUser.username
        binding.avatarText.text = activeUser.username.substring(0,2)
    }

    private fun observeAndUpdateHrTV() {
        hrViewModel.currentHrBpm.observe(viewLifecycleOwner) { newHrData ->
            binding.heartRate.text = newHrData.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeAndUpdateBatteryTV() {
        hrViewModel.currentBatteryStatus.observe(viewLifecycleOwner) { newBatteryStatus ->
            if (newBatteryStatus) {
                binding.batteryLevel.text = "Good"
                binding.batteryLevel.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
            } else {
                binding.batteryLevel.text = "Recharge"
                binding.batteryLevel.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeAndUpdateActivityTV() {
        hrViewModel.currentActivity.observe(viewLifecycleOwner) { newActivity ->
            binding.activityMonitor.text = "Activity: $newActivity"
        }
    }

    private fun subscribeToActiveUser() {
        ActiveUserEventBus.subscribe { activeUserChangeEvent ->
            onActiveUserChangeEvent(activeUserChangeEvent.user)
        }
    }

    private fun onActiveUserChangeEvent(user: User) {
        stopConnectionAndSetUI()
        activeUser = user
        startConnectionAndSetUI()
    }

}