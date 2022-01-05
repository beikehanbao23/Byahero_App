package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository
import com.example.commutingapp.feature_note.domain.util.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlaces(
    private val repository: PlaceRepository) {


    operator fun invoke(
        orderType: OrderType
    ): Flow<List<Place>> {

        return repository.getPlaceList().map { place->

            when(orderType){

                is OrderType.Descending -> place.sortedBy { it.placeName }

                is OrderType.Ascending -> place.sortedByDescending { it.placeName }

            }
        }

    }
}