package com.ham.activitymonitorapp.fragments

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.databinding.ExerciseFragmentBinding
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.exceptions.NoActiveUserException
import com.ham.activitymonitorapp.services.ExerciseService
import com.ham.activitymonitorapp.services.ServiceRunningChecker
import com.ham.activitymonitorapp.viewmodels.ExerciseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseFragment: Fragment(R.layout.exercise_fragment) {

    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private var _binding: ExerciseFragmentBinding? = null

    private val binding get() = _binding!!

    lateinit var exerciseService: ExerciseService

    private val serviceRunningChecker: ServiceRunningChecker = ServiceRunningChecker()

    private var connected = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ExerciseService.ExerciseServiceBinder
            exerciseService = binder.getService()
            connected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            connected = false
        }
    }

    companion object {
        const val TAG = "EXERCISE_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExerciseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAndUpdateExercise()
        observeActiveUser()
        handleStartButton()
        handleStopButton()
        enableOrDisableStartAndStopButton()
    }

    private fun observeAndUpdateExercise() {
        exerciseViewModel.currentExercise.observe(viewLifecycleOwner) { newExercise ->
            binding.includeExerciseStart.exercise = newExercise
            enableOrDisableStartAndStopButton()
        }
    }

    private fun observeActiveUser() {
        ActiveUserEventBus.subscribe {
            onActiveUserChangeEvent()
        }
    }

    private fun onActiveUserChangeEvent() {
        if (serviceRunningChecker.isServiceRunning(ExerciseService::class.java, requireContext())) {
            stopExercise()
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
        }
    }

    private fun startExercise(){
        try {
            exerciseViewModel.createExerciseWithBasicFields()

            val intent = Intent(context, ExerciseService::class.java)
            intent.putExtra("command", "start")

            requireContext().startForegroundService(intent)
            showToast("Exercise started")
            binding.includeExerciseStart.buttonStartExercise.isEnabled = false
            binding.includeExerciseStart.buttonStopExercise.isEnabled = true
        } catch (e: NoActiveUserException) {
            showToast(e.message.toString())
            Log.e(TAG, e.message.toString())
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
        }
    }

    private fun bindExerciseService() {
        val serviceIntent = Intent(requireContext(), ExerciseService::class.java)
        requireContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopExercise() {
        if (connected) {
            try {
                val serviceIntent = Intent(requireContext(), ExerciseService::class.java)
                requireContext().unbindService(serviceConnection)
                requireContext().stopService(serviceIntent)
                showToast("Exercise stopped")
                Log.d(TAG, "Exercise stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Exercise cannot be stopped")
            }
        } else {
            Log.d(TAG, "No service connection")
        }
    }

    private fun handleStartButton() {
        binding.includeExerciseStart.buttonStartExercise.setOnClickListener {
            startExercise()
            bindExerciseService()
        }
    }

    private fun handleStopButton() {
        binding.includeExerciseStart.buttonStopExercise.setOnClickListener {
            stopExercise()
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
        }
    }

    private fun enableOrDisableStartAndStopButton() {
        if (exerciseViewModel.currentExercise.value == null || exerciseViewModel.currentExercise.value!!.done) {
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
        } else {
            binding.includeExerciseStart.buttonStartExercise.isEnabled = false
            binding.includeExerciseStart.buttonStopExercise.isEnabled = true
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }
}