package com.ham.activitymonitorapp.data.repositories

import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.relationship.UserAndExercise
import com.ham.activitymonitorapp.data.relationship.UserAndHeartrate
import com.ham.activitymonitorapp.exception.UserNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
){
    private lateinit var activeUser: User

    fun getActiveUser(): User {
        return activeUser
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAll()
    }

    suspend fun setActiveUser(id: Int): User = withContext(Dispatchers.IO) {
        activeUser = userDao.getById(id)!!
        activeUser
    }

    suspend fun getUserById(id: Int): User? = withContext(Dispatchers.IO) {
        userDao.getById(id)
    }

    suspend fun deleteUser(user: User) = withContext(Dispatchers.IO) {
        if (getUserById(user.userId) == null) {
            throw UserNotFoundException(user.userId)
        } else {
            userDao.delete(user)
        }
    }

    private suspend fun createUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertAll(user)
    }

    private suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        userDao.updateUsers(user)
    }

    suspend fun createOrUpdateUser(user: User) = withContext(Dispatchers.IO) {
        val existingUser = getUserById(user.userId)

        if (existingUser == null) {
            createUser(user)
        } else {
            updateUser(user)
        }
    }

    suspend fun getUserAndExercises(id: Int): List<UserAndExercise> = withContext(Dispatchers.IO) {
        userDao.getUserAndExercises(id)
    }

    suspend fun getUserAndHrs(id: Int): List<UserAndHeartrate> = withContext(Dispatchers.IO) {
        userDao.getUserAndHeartrates(id)
    }
}