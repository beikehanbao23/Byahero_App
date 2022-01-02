package com.example.commutingapp.feature_note.data.data_source

import androidx.room.*
import com.example.commutingapp.feature_note.domain.model.Place
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {


    @Query("SELECT * FROM Place")
    fun getPlaceList(): Flow<List<Place>>

    @Query("SELECT * FROM Place WHERE placeName like '%' || :placeName || '%'")
    suspend fun getPlaceListByName(placeName:String):Place

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)


    @Delete
    suspend fun deletePlace(place:Place)

}