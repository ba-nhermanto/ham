package com.ham.activitymonitorapp.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ham.activitymonitorapp.data.dao.HeartrateDao
import com.ham.activitymonitorapp.data.dao.UserDao
import com.ham.activitymonitorapp.data.database.HamDatabase
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.data.repositories.HeartrateRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.sql.Date
import java.sql.Timestamp

@RunWith(AndroidJUnit4::class)
class HeartrateRepositoryInstrumentedTest {

    private lateinit var hrDao: HeartrateDao
    private lateinit var userDao: UserDao
    private lateinit var heartrateRepository: HeartrateRepository
    private lateinit var database: HamDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HamDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        hrDao = database.heartrateDao()
        userDao = database.userDao()
        heartrateRepository = HeartrateRepository(hrDao)

        insertUser()
    }

    @After
    fun cleanup() {
        cleanupDb()
        database.close()
    }

    private fun cleanupDb() = runBlocking {
        val hrs = heartrateRepository.getAllHr()
        hrs.forEach { heartrateRepository.deleteHr(it) }
    }

    private fun insertUser() = runBlocking {
        userDao.insert(
            User(
                1L,
                username = "John Doe",
                weight = 60,
                dateOfBirth = Date.valueOf("2000-04-30"),
                deviceId = "DEVICE_ID",
                gender = Gender.MALE
            )
        )
    }

    @Test
    fun testGetHrById() = runBlocking {
        // Arrange
        val heartrate = supplyHr()
        heartrateRepository.save(heartrate)

        // Act
        val result = heartrateRepository.getHrById(heartrate.hrId)

        // Assert
        assertEquals(heartrate, result)
    }

    @Test
    fun testGetAllHrById() = runBlocking {
        // Arrange
        val heartrates = supplyListOfHr()
        heartrates.forEach { heartrateRepository.save(it) }
        val hrIds = heartrates.map { it.hrId }.toLongArray()

        // Act
        val result = heartrateRepository.getAllHrById(hrIds)

        // Assert
        assertEquals(heartrates, result)
    }

    @Test
    fun testUpdateHr() = runBlocking {
        // Arrange
        val heartrate = supplyHr()
        heartrateRepository.save(heartrate)

        // Update heartrate value
        val updatedHeartrate = supplyHr()
        updatedHeartrate.bpm = 120

        // Act
        heartrateRepository.updateHr(updatedHeartrate)

        // Assert
        val result = heartrateRepository.getHrById(heartrate.hrId)
        assertEquals(updatedHeartrate, result)
    }

    @Test
    fun testDeleteHr() = runBlocking {
        // Arrange
        val heartrate = supplyHr()
        heartrateRepository.save(heartrate)

        // Act
        heartrateRepository.deleteHr(heartrate)

        // Assert
        val result = heartrateRepository.getHrById(heartrate.hrId)
        assertNull(result)
    }

    private fun supplyHr(): Heartrate {
        return Heartrate(
            hrId = 1L,
            userId = 1L,
            bpm = 80,
            timestamp = Timestamp.valueOf("2023-06-15 20:45:00"),
        )
    }

    private fun supplyListOfHr(): List<Heartrate> {
        return listOf(
            supplyHr(),
            Heartrate(
                hrId = 2L,
                userId = 1L,
                bpm = 70,
                timestamp = Timestamp.valueOf("2023-06-13 20:45:00"),
            ),
            Heartrate(
                hrId = 3L,
                userId = 1L,
                bpm = 87,
                timestamp = Timestamp.valueOf("2023-06-14 20:45:00"),
            )
        )
    }
}
