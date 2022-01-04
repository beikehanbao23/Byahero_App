package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository
import com.example.commutingapp.feature_note.domain.util.OrderType
import com.example.commutingapp.feature_note.domain.util.PlaceOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlace(
    private val repository: PlaceRepository) {


    operator fun invoke(
        placeOrder: PlaceOrder = PlaceOrder.PlaceName(OrderType.Descending)
    ): Flow<List<Place>>{

        return repository.getPlaceList().map { place->

            when(placeOrder.orderType){

                is OrderType.Ascending -> {

                    when(placeOrder){
                        is PlaceOrder.PlaceName-> place.sortedBy { it.placeName.lowercase() }
                        is PlaceOrder.PlaceText-> place.sortedBy { it.placeText.lowercase() }

                    }
                }


                is OrderType.Descending -> {

                    when(placeOrder){
                        is PlaceOrder.PlaceName-> place.sortedByDescending { it.placeName.lowercase() }
                        is PlaceOrder.PlaceText-> place.sortedByDescending { it.placeText.lowercase() }

                    }
                }


            }
        }

    }
}