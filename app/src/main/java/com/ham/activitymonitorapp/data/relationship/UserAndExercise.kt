package com.ham.activitymonitorapp.data.relationship

import androidx.room.Embedded
import androidx.room.Relation
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.data.entities.User

data class UserAndExercise(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val exercises: List<Exercise>
)