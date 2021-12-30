package com.example.commutingapp.feature_note.data.repository

import com.example.commutingapp.feature_note.data.data_source.PlaceDao
import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository
import kotlinx.coroutines.flow.Flow

class PlaceRepositoryImpl(
private val dao: PlaceDao
):PlaceRepository {

    override fun getSaveList(): Flow<List<Place>> {
        return dao.getPlaceList()
    }

    override suspend fun getSaveListByPlaceName(placeName: String): Place {
       return dao.getPlaceListByName(placeName)
    }

    override suspend fun insertPlace(place: Place) {
        dao.insertPlace(place)
    }

    override suspend fun deletePlace(place: Place) {
        dao.deletePlace(place)
    }
}