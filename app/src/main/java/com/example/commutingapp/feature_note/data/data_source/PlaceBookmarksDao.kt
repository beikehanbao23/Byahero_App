package com.example.commutingapp.feature_note.data.data_source

import androidx.room.*
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceBookmarksDao {


    @Query("SELECT * FROM PlaceBookmarks")
    fun getPlaceList(): Flow<List<PlaceBookmarks>>

    @Query("SELECT * FROM PlaceBookmarks WHERE placeName like '%' || :placeName || '%'")
    suspend fun getPlaceListByName(placeName:String):PlaceBookmarks

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(placeBookmarks: PlaceBookmarks)


    @Delete
    suspend fun deletePlace(placeBookmarks:PlaceBookmarks)

}