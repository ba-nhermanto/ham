package com.ham.activitymonitorapp.repositories

import com.ham.activitymonitorapp.data.dao.HeartrateDao
import com.ham.activitymonitorapp.data.entities.Heartrate
import com.ham.activitymonitorapp.data.repositories.HeartrateRepository
import com.ham.activitymonitorapp.exception.HrNotFoundException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.sql.Timestamp

@RunWith(JUnit4::class)
class HrRepositoryTest {

    @Mock
    private lateinit var hrDao: HeartrateDao

    private lateinit var hrRepository: HeartrateRepository

    private val USER_ID = 1
    private val HR_ID = 1

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hrRepository = HeartrateRepository(hrDao)
    }

    @Test
    fun `test getHrById`() = runTest {
        // Setup
        val hr = supplyHr()
        `when`(hrDao.getById(HR_ID)).thenReturn(hr)

        // Act
        val result = hrRepository.getHrById(HR_ID)

        // Assert
        assertEquals(hr, result)
        verify(hrDao).getById(HR_ID)
    }

    @Test
    fun `test getAllHrById`() = runTest {
        // Setup
        val hr = supplyHr()
        `when`(hrDao.loadAllByIds(IntArray(HR_ID))).thenReturn(listOf(hr))

        // Act
        val result = hrRepository.getAllHrById(IntArray(HR_ID))

        // Assert
        assertEquals(listOf(hr), result)
        verify(hrDao).loadAllByIds(IntArray(HR_ID))
    }

    @Test
    fun `test save`() = runTest {
        // Setup
        val hr = supplyHr()

        // Act
        hrRepository.save(hr)

        // Assert
        verify(hrDao).insertAll(hr)
    }

    @Test
    fun `test delete hr not found`() = runTest {
        // Setup
        val hr = supplyHr()
        `when`(hrDao.getById(HR_ID)).thenReturn(null)

        try {
            // Act
            hrRepository.deleteHr(hr)
            fail("Expected HrNotFoundException to be thrown")
        } catch (e: HrNotFoundException) {
            // Assert
            assertEquals("Hr with hrId $HR_ID not found", e.message)
            verify(hrDao, never()).delete(hr)
        }
    }

    @Test
    fun `test delete hr`() = runTest {
        // Setup
        val hr = supplyHr()
        `when`(hrDao.getById(HR_ID)).thenReturn(null)

        try {
            // Act
            hrRepository.deleteHr(hr)
        } catch (e: HrNotFoundException) {
            // Assert
            verify(hrDao, never()).delete(hr)
        }
    }

    @Test
    fun `test updateHr`() = runTest {
        // Setup
        val hr = supplyHr()
        `when`(hrDao.getById(HR_ID)).thenReturn(hr)

        // Act
        hrRepository.updateHr(hr)

        // Assert
        verify(hrDao).update(hr)
    }

    private fun supplyHr(): Heartrate {
        return Heartrate(
            hrId = HR_ID,
            userId = USER_ID,
            bpm = 80,
            timestamp = Timestamp.valueOf("2023-06-12 20:45:00")
        )
    }
}

