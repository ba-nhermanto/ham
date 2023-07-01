package com.ham.activitymonitorapp.fragments

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.databinding.ExerciseFragmentBinding
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.services.ExerciseService
import com.ham.activitymonitorapp.services.ServiceManager
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

    private val serviceManager: ServiceManager = ServiceManager()

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
            serviceManager.stopExercise(connected, requireContext(), serviceConnection)
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
        }
    }

    private fun handleStartButton() {
        binding.includeExerciseStart.buttonStartExercise.setOnClickListener {
            serviceManager.startExercise(exerciseViewModel, requireContext(), binding)
            serviceManager.bindExerciseService(requireContext(), serviceConnection)
        }
    }

    private fun handleStopButton() {
        binding.includeExerciseStart.buttonStopExercise.setOnClickListener {
            serviceManager.stopExercise(connected, requireContext(), serviceConnection)
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

}