package com.ham.activitymonitorapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.ham.activitymonitorapp.data.repositories.UserRepository
import javax.inject.Inject

class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    application: Application
) : AndroidViewModel(application) {
}