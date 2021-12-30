package com.example.commutingapp.feature_note.data.data_source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.commutingapp.feature_note.domain.model.Place
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE


@Dao
interface PlaceDao {


    @Query("SELECT * FROM Place")
    fun getPlaceList(): Flow<List<Place>>

    @Query("SELECT * FROM Place WHERE placeName like '%' || :placeName || '%'")
    suspend fun getPlaceListByName(placeName:String):Place

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)


    @DELETE
    suspend fun deletePlace(place:Place)

}