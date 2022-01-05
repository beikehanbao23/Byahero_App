package com.example.commutingapp.feature_note.domain.repository

import com.example.commutingapp.feature_note.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getPlaceList(): Flow<List<Place>>
    suspend fun getPlaceListByName(placeName:String):Place
    suspend fun insertPlace(place: Place)
    suspend fun deletePlace(place:Place)

}