package com.example.commutingapp.feature_note.data.repository

import com.example.commutingapp.feature_note.data.data_source.PlaceBookmarksDao
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository
import kotlinx.coroutines.flow.Flow

class PlaceBookmarksRepositoryImpl(
private val bookmarksDao: PlaceBookmarksDao
):PlaceBookmarksRepository {

    override fun getPlaceList(): Flow<List<PlaceBookmarks>> {
        return bookmarksDao.getPlaceList()
    }

    override suspend fun insertPlace(placeBookmarks: PlaceBookmarks) {
        bookmarksDao.insertPlace(placeBookmarks)
    }

    override suspend fun deletePlace(placeBookmarks: PlaceBookmarks) {
        bookmarksDao.deletePlace(placeBookmarks)
    }
}