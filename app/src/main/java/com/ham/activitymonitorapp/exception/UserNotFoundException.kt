package com.ham.activitymonitorapp.exception

class UserNotFoundException(userId: Int) : Exception("User with userId $userId not found")
