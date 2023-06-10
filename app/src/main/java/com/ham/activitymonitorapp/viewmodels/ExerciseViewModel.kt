package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ham.activitymonitorapp.data.repositories.ExerciseRepository
import javax.inject.Inject

class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    application: Application
) : AndroidViewModel(application) {
}