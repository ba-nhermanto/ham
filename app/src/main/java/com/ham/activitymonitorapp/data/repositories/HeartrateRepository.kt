package com.ham.activitymonitorapp.data.repositories

import com.ham.activitymonitorapp.data.dao.HeartrateDao
import com.ham.activitymonitorapp.data.entities.Heartrate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HeartrateRepository @Inject constructor(
    private val hrDao: HeartrateDao
){
    suspend fun getHrById(id: Int): Heartrate = withContext(Dispatchers.IO) {
        hrDao.getById(id)
    }

    suspend fun getAllHrById(ids: IntArray): List<Heartrate> = withContext(Dispatchers.IO) {
        hrDao.loadAllByIds(ids)
    }

    suspend fun save(hr: Heartrate) = withContext(Dispatchers.IO) {
        hrDao.insertAll(hr)
    }

    suspend fun update(hr: Heartrate) = withContext(Dispatchers.IO) {
        hrDao.update(hr)
    }
}