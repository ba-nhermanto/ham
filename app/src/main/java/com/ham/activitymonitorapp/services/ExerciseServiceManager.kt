package com.ham.activitymonitorapp.services

import android.content.Context
import android.content.Intent
import android.util.Log
import com.ham.activitymonitorapp.databinding.ExerciseFragmentBinding
import com.ham.activitymonitorapp.exceptions.NoActiveUserException
import com.ham.activitymonitorapp.fragments.ExerciseFragment
import com.ham.activitymonitorapp.view.Toaster
import com.ham.activitymonitorapp.viewmodels.ExerciseViewModel

class ExerciseServiceManager {

    private val toaster: Toaster = Toaster()

    fun stopExercise(context: Context) {
        try {
            val serviceIntent = Intent(context, ExerciseService::class.java)
            context.stopService(serviceIntent)
            toaster.showToast("Exercise stopped", context)
            Log.d(ExerciseFragment.TAG, "Exercise stopped")
        } catch (e: Exception) {
            Log.e(ExerciseFragment.TAG, "Exercise cannot be stopped: ${e.message.toString()}")
        }
    }

    fun startExercise(exerciseViewModel: ExerciseViewModel, context: Context, binding: ExerciseFragmentBinding){
        try {
            exerciseViewModel.createExerciseWithBasicFields()

            val intent = Intent(context, ExerciseService::class.java)
            intent.putExtra("command", "start")

            context.startForegroundService(intent)
            toaster.showToast("Exercise started", context)
            binding.includeExerciseStart.buttonStartExercise.isEnabled = false
            binding.includeExerciseStart.buttonStopExercise.isEnabled = true
        } catch (e: NoActiveUserException) {
            toaster.showToast(e.message.toString(), context)
            Log.e(ExerciseFragment.TAG, e.message.toString())
            binding.includeExerciseStart.buttonStartExercise.isEnabled = true
            binding.includeExerciseStart.buttonStopExercise.isEnabled = false
        }
    }

}