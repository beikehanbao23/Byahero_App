package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository
import com.example.commutingapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlacesFromBookmarks(
    private val bookmarksRepository: PlaceBookmarksRepository
) {


     operator fun invoke(
        input: OrderType
    ): Flow<List<PlaceBookmarks>> {

        return bookmarksRepository.getPlaceList().map { place->
            when(input){
                is OrderType.Descending -> place.sortedByDescending { it.placeName }
                is OrderType.Ascending -> place.sortedBy { it.placeName }

            }
        }

    }
}