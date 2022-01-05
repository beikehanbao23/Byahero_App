package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository

class GetPlaceByNameFromBookmarks (
    private val bookmarksRepository: PlaceBookmarksRepository){

    suspend operator fun invoke(placeName:String):PlaceBookmarks{
        return bookmarksRepository.getPlaceListByName(placeName)
    }

}