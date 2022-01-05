package com.example.commutingapp.feature_note.presentation.place.components

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.util.OrderType

sealed class PlaceEvent {
    data class ChangeOrder(val orderType: OrderType):PlaceEvent()
    data class DeletePlace(val place:Place):PlaceEvent()
    object RestoreNote:PlaceEvent()
    data class SearchPlace(val placeName:String):PlaceEvent()
}