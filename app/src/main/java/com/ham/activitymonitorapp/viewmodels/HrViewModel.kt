package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ham.activitymonitorapp.data.repositories.HeartrateRepository
import javax.inject.Inject

class HrViewModel @Inject constructor(
    private val hrRepository: HeartrateRepository,
    application: Application
) : AndroidViewModel(application) {
}