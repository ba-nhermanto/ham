package com.ham.activitymonitorapp.repositories

import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.relationship.UserAndExercise
import com.ham.activitymonitorapp.data.relationship.UserAndHeartrate
import com.ham.activitymonitorapp.data.repositories.UserRepository
import com.ham.activitymonitorapp.exceptions.UserNotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.sql.Date

@RunWith(JUnit4::class)
class UserRepositoryTest {

    @Mock
    private lateinit var userDao: UserDao

    private lateinit var userRepository: UserRepository

    private val USER_ID = 1L

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `test getUsers`() = runTest {
        // Setup
        val users = listOf(supplyUser(),
            User(2, "USER_2", 70, Date.valueOf("2001-05-21"),  Gender.FEMALE,"DEVICE_ID_2"))
        `when`(userDao.getAll()).thenReturn(users)

        // Act
        val result = userRepository.getUsers()

        // Assert
        assertEquals(users, result)
        verify(userDao).getAll()
    }


    @Test
    fun `test getUserById() function`(): Unit =  runTest {
        val user = supplyUser()
        val expectedUser = user

        `when`(userDao.getById(USER_ID)).thenReturn(expectedUser)

        // Act
        val result = userRepository.getUserById(USER_ID)

        // Assert
        assertEquals(user, result)
        verify(userDao).getById(USER_ID)
    }

    @Test
    fun `test deleteUserById`() = runTest {
        // Setup
        val user = supplyUser()
        `when`(userDao.getById(USER_ID)).thenReturn(user)

        // Act
        userRepository.deleteUser(user)

        // Assert
        verify(userDao).delete(user)
    }

    @Test
    fun `test deleteNonExistingUser`() = runTest {
        // Setup
        val user = supplyUser()
        `when`(userDao.getById(USER_ID)).thenReturn(null)

        try {
            // Act
            userRepository.deleteUser(user)
            fail("Expected UserNotFoundException to be thrown")
        } catch (e: UserNotFoundException) {
            // Assert
            assertEquals("User with userId $USER_ID not found", e.message)
            verify(userDao, never()).delete(user)
        }
    }

    @Test
    fun `test createUser`() = runTest {
        // Setup
        val user = supplyUser()

        // Act
        userRepository.upsertUser(user)

        // Assert
        verify(userDao).insert(user)
    }

    @Test
    fun `test updateUser`() = runTest {
        // Setup
        val user = supplyUser()
        `when`(userDao.getById(user.userId)).thenReturn(user)

        // Act
        userRepository.upsertUser(user)

        // Assert
        verify(userDao).insert(user)
    }

    @Test
    fun `test getUserAndExercises`() = runTest {
        // Setup
        val userAndExercises = listOf(
            UserAndExercise(user = supplyUser(), exercises = emptyList())
        )
        `when`(userDao.getUserAndExercises(USER_ID)).thenReturn(userAndExercises)

        // Act
        val result = userRepository.getUserAndExercises(USER_ID)

        // Assert
        assertEquals(userAndExercises, result)
        verify(userDao).getUserAndExercises(USER_ID)
    }

    @Test
    fun `test getUserAndHrs`() = runTest {
        // Setup
        val userAndHeartrates = listOf(
            UserAndHeartrate(user = supplyUser(), heartrates = emptyList())
        )
        `when`(userDao.getUserAndHeartrates(USER_ID)).thenReturn(userAndHeartrates)

        // Act
        val result = userRepository.getUserAndHrs(USER_ID)

        // Assert
        assertEquals(userAndHeartrates, result)
        verify(userDao).getUserAndHeartrates(USER_ID)
    }

    @Test
    fun `test setUserActiveThenGetActiveUser`() = runTest {
        // Setup
        val user = supplyUser()
        `when`(userDao.getActiveUser()).thenReturn(user)

        // Act
        userRepository.setActiveUser(USER_ID)
        val activeUser = userRepository.getActiveUser()

        // Assert
        assertEquals(activeUser, user)
        verify(userDao).setActiveUser(USER_ID)
        verify(userDao, times(2)).getActiveUser()
    }

    private fun supplyUser(): User {
        return User(USER_ID,
            username = "John Doe",
            weight = 60,
            dateOfBirth = Date.valueOf("2000-04-30"),
            deviceId = "DEVICE_ID",
            gender = Gender.MALE
        )
    }
}

