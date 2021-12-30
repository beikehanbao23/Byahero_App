package com.example.commutingapp.feature_note.domain.repository

import com.example.commutingapp.feature_note.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getSaveList(): Flow<List<Place>>
    suspend fun getSaveListByPlaceName(placeName:String):Place
    suspend fun insertPlace(place: Place)
    suspend fun deletePlace(place:Place)
}