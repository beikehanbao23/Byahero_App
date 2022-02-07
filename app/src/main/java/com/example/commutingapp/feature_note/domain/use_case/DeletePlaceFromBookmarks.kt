package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository

class DeletePlaceFromBookmarks(
    private val bookmarksRepository: PlaceBookmarksRepository ) {

     suspend operator fun invoke(input:PlaceBookmarks){
        bookmarksRepository.deletePlace(input)
    }

}