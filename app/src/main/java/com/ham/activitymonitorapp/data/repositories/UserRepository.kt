package com.ham.activitymonitorapp.data.repositories

import androidx.room.Transaction
import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.relationship.UserAndExercise
import com.ham.activitymonitorapp.data.relationship.UserAndHeartrate
import com.ham.activitymonitorapp.exceptions.UserNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
){
    suspend fun getActiveUser(): User = withContext(Dispatchers.IO) {
        userDao.getActiveUser()
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        userDao.getAll()
    }

    @Transaction
    suspend fun setActiveUser(id: Long): User = withContext(Dispatchers.IO) {
        setAllUserInactive()
        userDao.setActiveUser(id)
        getActiveUser()
    }

    private suspend fun setAllUserInactive() = withContext(Dispatchers.IO) {
        userDao.setAllUserInactive()
    }

    suspend fun getUserById(id: Long): User? = withContext(Dispatchers.IO) {
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

    suspend fun upsertUser(user: User): User? = withContext(Dispatchers.IO) {
        val userId = userDao.insert(user)
        getUserById(userId)
    }

    suspend fun getUserAndExercises(id: Long): List<UserAndExercise> = withContext(Dispatchers.IO) {
        userDao.getUserAndExercises(id)
    }

    suspend fun getUserAndHrs(id: Long): List<UserAndHeartrate> = withContext(Dispatchers.IO) {
        userDao.getUserAndHeartrates(id)
    }

    suspend fun getListOfHrBpmByUserId(id: Long): List<Int> = withContext(Dispatchers.IO) {
        userDao.getUserAndHeartrates(id).flatMap { userAndHrs ->
            userAndHrs.heartrates.map { hr -> hr.bpm }
        }
    }
}