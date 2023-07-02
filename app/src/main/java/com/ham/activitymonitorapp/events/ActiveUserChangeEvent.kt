package com.ham.activitymonitorapp.events

import com.ham.activitymonitorapp.data.entities.User

data class ActiveUserChangeEvent (
    val user: User?
)
