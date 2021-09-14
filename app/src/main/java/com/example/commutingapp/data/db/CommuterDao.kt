package com.example.commutingapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CommuterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommuter(commuter: Commuter)

    @Delete
    suspend fun deleteCommuter(commuter: Commuter)



@Query("""
SELECT * FROM commuter_table
ORDER BY 
 CASE WHEN :column = 'TIMESTAMP' THEN timestamp END DESC,
 CASE WHEN :column = 'TIME_IN_MILLIS' THEN timeInMillis END DESC,
 CASE WHEN :column = 'AVERAGE_SPEED' THEN averageSpeed_KmH END DESC,
 CASE WHEN :column = 'DISTANCE' THEN distanceInMeters END DESC,
 CASE WHEN :column = 'PLACES' THEN wentPlaces END DESC
""")
   fun filterBy(column:String):LiveData<List<Commuter>>



    @Query("SELECT SUM(timeInMillis) FROM commuter_table")
    fun getTotalTimeInMillis():LiveData<Long>

    @Query("SELECT AVG(averageSpeed_KmH) FROM commuter_table")
    fun getTotalAverageSpeed():LiveData<Float>

    @Query("SELECT SUM(distanceInMeters)FROM commuter_table")
    fun getTotalDistance():LiveData<Int>
}