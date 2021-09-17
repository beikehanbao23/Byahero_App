package com.example.commutingapp.data.repositories

import androidx.lifecycle.LiveData
import com.example.commutingapp.data.local_db.Commuter
import com.example.commutingapp.data.local_db.CommuterDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val commuterDao: CommuterDao,
) {

    suspend fun insertCommuter(commuter: Commuter) =
        commuterDao.insertCommuter(commuter)

    suspend fun deleteCommuter(commuter: Commuter) =
        commuterDao.deleteCommuter(commuter)

    fun filterBy(column: String): LiveData<List<Commuter>> =
        commuterDao.filterBy(column)

    fun getTotalTimeInMillis(): LiveData<Long> =
        commuterDao.getTotalTimeInMillis()

    fun getTotalAverageSpeed(): LiveData<Float> =
        commuterDao.getTotalAverageSpeed()

    fun getTotalDistance(): LiveData<Int> =
        commuterDao.getTotalDistance()
}