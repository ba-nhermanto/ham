package com.ham.activitymonitorapp.exceptions

class UserNotFoundException(userId: Long) : Exception("User with userId $userId not found")
