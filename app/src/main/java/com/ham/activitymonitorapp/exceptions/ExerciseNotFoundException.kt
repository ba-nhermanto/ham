package com.ham.activitymonitorapp.exceptions

class ExerciseNotFoundException(exerciseId: Long) : Exception("Exercise with exerciseId $exerciseId not found")
