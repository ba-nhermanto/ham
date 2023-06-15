package com.ham.activitymonitorapp.exceptions

class UserNotFoundException(userId: Int) : Exception("User with userId $userId not found")
