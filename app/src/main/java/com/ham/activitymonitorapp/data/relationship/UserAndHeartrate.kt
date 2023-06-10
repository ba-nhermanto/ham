package com.ham.activitymonitorapp.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.entities.User

data class UserAndHeartrate(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val heartrates: List<Heartrate>
)