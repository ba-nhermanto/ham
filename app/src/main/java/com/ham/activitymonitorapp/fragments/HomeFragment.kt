package com.ham.activitymonitorapp.fragments

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.HomeFragmentBinding
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.exceptions.NoActiveUserException
import com.ham.activitymonitorapp.services.*
import com.ham.activitymonitorapp.viewmodels.HrViewModel
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.*

@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.home_fragment) {
    private val userViewModel: UserViewModel by viewModels()

    private val hrViewModel: HrViewModel by viewModels()

    private var _binding: HomeFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var activityService: ActivityService

    private var connectionService: ConnectionService? = null

    private var activeUser: User? = null

    private val serviceRunningChecker: ServiceRunningChecker = ServiceRunningChecker()

    private var connected: Boolean = false

    private val connectionServiceManager: ConnectionServiceManager = ConnectionServiceManager()

    private lateinit var activity: FragmentActivity

    private lateinit var graphService: GraphService

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ConnectionService.ConnectionServiceBinder
            connectionService = binder.getService()
            connected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            connected = false
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

        activity = requireActivity()

        graphService = GraphService(binding, hrViewModel, activity, viewLifecycleOwner)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getActiveUser()

        observeActiveUser()
//        hrViewModel.initActiveUserAndListHr()
//
//        runBlocking {
//        }

        if (activeUser != null) {
            Log.d(TAG, "home fragment init")
            handleConnect()
            showUsername()
            showDeviceId()
            observeHrData()
            observeAndUpdateBatteryTV()
            observeAndUpdateActivityTV()
            graphService.initializeHrGraph()
            graphService.observeHr()
        } else {
            binding.materialSwitch.isEnabled = false
        }

        changeConnectedText()

    }

    private fun handleConnect() {
        binding.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!serviceRunningChecker.isServiceRunning(ConnectionService::class.java, activity)) {
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
        try {
            connectionServiceManager.startConnectionService(activity, activeUser!!)
            connectionServiceManager.bindConnectionService(activity, serviceConnection)
            connected = true
        } catch (e: NoActiveUserException) {
            Log.e(TAG, e.message.toString())
        }
        binding.materialSwitch.isChecked = true
        binding.connectText.text = resources.getString(R.string.connected)
        binding.connectText.setTextColor(ContextCompat.getColor(activity, R.color.teal_200))
    }

    private fun stopConnectionAndSetUI() {
        if (serviceRunningChecker.isServiceRunning(ConnectionService::class.java, activity)) {
            Log.d(TAG, "stopping connection service")
            connectionServiceManager.stopConnectionService(activity, serviceConnection, connected)
            connected = false
            binding.materialSwitch.isChecked = false
            binding.connectText.text = resources.getString(R.string.disconnected)
            binding.connectText.setTextColor(ContextCompat.getColor(activity, R.color.red))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDeviceId() {
        binding.connectionDevice.text = "Polar ${activeUser!!.deviceId}"
    }

    private fun getActiveUser() {
        activeUser = runBlocking {
            userViewModel.getActiveUser()
        }
        Log.d(TAG, "active user: $activeUser")
    }

    private fun showUsername() {
        binding.username.text = activeUser!!.username
        binding.avatarText.text = activeUser!!.username.substring(0,2)
    }

    private fun observeHrData() {
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

    private fun observeActiveUser() {
        ActiveUserEventBus.subscribe { activeUserChangeEvent ->
            activeUserChangeEvent.user?.let { onActiveUserChangeEvent(it) }
        }
    }

    private fun onActiveUserChangeEvent(user: User) {
        Log.d(TAG, "active user changed: $user")
        stopConnectionAndSetUI()
        activeUser = user
        graphService.initializeHrGraph()
    }

    private fun changeConnectedText() {
        if (connected) {
            binding.connectText.text = resources.getString(R.string.connected)
            binding.connectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
        } else {
            binding.connectText.text = resources.getString(R.string.disconnected)
            binding.connectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }

}