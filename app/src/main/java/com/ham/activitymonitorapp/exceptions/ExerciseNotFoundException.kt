package com.ham.activitymonitorapp.exceptions

class ExerciseNotFoundException(exerciseId: Int) : Exception("Exercise with exerciseId $exerciseId not found")
