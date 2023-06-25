package com.ham.activitymonitorapp.repositories

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.database.HamDatabase
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.sql.Date

@RunWith(AndroidJUnit4::class)
class UserRepositoryInstrumentedTest {

    private lateinit var userDao: UserDao
    private lateinit var userRepository: UserRepository
    private lateinit var database: HamDatabase

    private val USER_ID = 1L
    private val DEVICE_ID = "DEVICE_ID"

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, HamDatabase::class.java).build()
        userDao = database.userDao()
        userRepository = UserRepository(userDao)
    }

    @After
    fun cleanup() {
        cleanupDb()
        database.close()
    }

    private fun cleanupDb() = runBlocking {
        val users = userRepository.getUsers()
        users.forEach { userRepository.deleteUser(it) }
    }

    @Test
    fun testGetUsers() = runBlocking {
        // Setup
        val users = listOf(
            supplyUser(),
            User(2, "User 2", 25, Date.valueOf("1990-01-01"), Gender.MALE, DEVICE_ID),
            User(3, "User 3", 30, Date.valueOf("1985-01-01"), Gender.FEMALE, DEVICE_ID)
        )
        users.forEach { userRepository.upsertUser(it) }

        // Act
        val result = userRepository.getUsers()

        // Assert
        assertEquals(users, result)
    }

    @Test
    fun testCreateOrUpdateUser_create_user(): Unit = runBlocking {
        // Setup
        val user = supplyUser()

        // Act
        userRepository.upsertUser(user)

        // Assert
        val result = userRepository.getUserById(USER_ID)
        assertEquals(user, result)
    }

    @Test
    fun testCreateOrUpdateUser_update_user(): Unit = runBlocking {
        // Setup
        val user = supplyUser()
        val updateUser = supplyUser()
        updateUser.weight = 65

        // Act
        userRepository.upsertUser(user)
        userRepository.upsertUser(updateUser)

        // Assert
        val result = userRepository.getUserById(USER_ID)
        assertEquals(updateUser, result)
    }

    @Test
    fun testGetUserAndExercises() = runBlocking {
        // Setup
        val user = supplyUser()
        userDao.insertAll(user)

        // Act
        val result = userRepository.getUserAndExercises(user.userId)

        // Assert
        assertEquals(1, result.size)
        assertEquals(user, result[0].user)
        assertTrue(result[0].exercises.isEmpty())
    }

    @Test
    fun testGetUserAndHrs() = runBlocking {
        // Setup
        val user = supplyUser()
        userDao.insertAll(user)

        // Act
        val result = userRepository.getUserAndHrs(user.userId)

        // Assert
        assertEquals(1, result.size)
        assertEquals(user, result[0].user)
        assertTrue(result[0].heartrates.isEmpty())
    }


    private fun supplyUser(): User {
        return User(
            userId = USER_ID,
            username = "John Doe",
            weight = 70,
            dateOfBirth = Date.valueOf("2000-01-01"),
            gender = Gender.MALE,
            deviceId = DEVICE_ID
        )
    }

}
