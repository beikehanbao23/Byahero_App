package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.InvalidPlaceException
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository

data class AddPlaceToBookmarks(
    private val bookmarksRepository: PlaceBookmarksRepository) {

    suspend operator fun invoke(placeBookmarks:PlaceBookmarks){

        if(placeBookmarks.placeName.isBlank() || placeBookmarks.placeText.isBlank()){
            throw InvalidPlaceException("Insertion Failed, place name or place text is invalid")
        }
        bookmarksRepository.insertPlace(placeBookmarks)
    }
}