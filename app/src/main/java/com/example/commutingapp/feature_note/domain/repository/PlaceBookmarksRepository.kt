package com.example.commutingapp.feature_note.domain.repository

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import kotlinx.coroutines.flow.Flow

interface PlaceBookmarksRepository {
    fun getPlaceList(): Flow<List<PlaceBookmarks>>
    suspend fun insertPlace(placeBookmarks: PlaceBookmarks)
    suspend fun deletePlace(placeBookmarks:PlaceBookmarks)

}