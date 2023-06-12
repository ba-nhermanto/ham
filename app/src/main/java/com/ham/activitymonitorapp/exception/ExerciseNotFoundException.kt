package com.ham.activitymonitorapp.exception

class ExerciseNotFoundException(exerciseId: Int) : Exception("Exercise with exerciseId $exerciseId not found")
