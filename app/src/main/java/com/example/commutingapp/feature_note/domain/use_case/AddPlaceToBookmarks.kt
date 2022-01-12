package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.InvalidPlaceException
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository

data class AddPlaceToBookmarks(
    private val bookmarksRepository: PlaceBookmarksRepository
    ):IAsyncUseCase<PlaceBookmarks> {

    override suspend operator fun invoke(input:PlaceBookmarks){

        if(input.placeName.isBlank() || input.placeText.isBlank()){
            throw InvalidPlaceException("Insertion Failed, place name or place text is invalid")
        }
        bookmarksRepository.insertPlace(input)
    }
}