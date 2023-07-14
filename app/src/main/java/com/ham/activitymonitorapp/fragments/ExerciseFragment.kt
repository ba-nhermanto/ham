package com.ham.activitymonitorapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.databinding.ExerciseFragmentBinding
import com.ham.activitymonitorapp.events.ActiveUserEventBus
import com.ham.activitymonitorapp.services.ExerciseService
import com.ham.activitymonitorapp.services.ExerciseServiceManager
import com.ham.activitymonitorapp.services.ServiceRunningChecker
import com.ham.activitymonitorapp.viewmodels.ExerciseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ExerciseFragment: Fragment(R.layout.exercise_fragment) {

    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private var _binding: ExerciseFragmentBinding? = null

    private val binding get() = _binding!!

    private val serviceRunningChecker: ServiceRunningChecker = ServiceRunningChecker()

    private val exerciseServiceManager: ExerciseServiceManager = ExerciseServiceManager()

    private lateinit var activity: FragmentActivity

    companion object {
        const val TAG = "EXERCISE_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExerciseFragmentBinding.inflate(inflater, container, false)
        activity = requireActivity()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runBlocking {
            exerciseViewModel.setActiveUser()
            exerciseViewModel.initExerciseList()
        }

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
            Log.d(TAG, "user change event received: ${it.user?.userId}")
            onActiveUserChangeEvent()
        }
    }

    private fun onActiveUserChangeEvent() {
        if (serviceRunningChecker.isServiceRunning(ExerciseService::class.java, activity)) {
            try {
                Log.d(TAG, "active user is changed, stopping exercise")
                exerciseServiceManager.stopExercise(activity)
                binding.includeExerciseStart.buttonStopExercise.isEnabled = false
                binding.includeExerciseStart.buttonStartExercise.isEnabled = true
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
        binding.includeExerciseStart.exercise = null
    }

    private fun handleStartButton() {
        binding.includeExerciseStart.buttonStartExercise.setOnClickListener {
            exerciseServiceManager.startExercise(exerciseViewModel, (activity), binding)
        }
    }

    private fun handleStopButton() {
        binding.includeExerciseStart.buttonStopExercise.setOnClickListener {
            exerciseServiceManager.stopExercise((activity))
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