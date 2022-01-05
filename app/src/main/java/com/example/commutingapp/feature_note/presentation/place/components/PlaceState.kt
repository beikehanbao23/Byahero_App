package com.example.commutingapp.feature_note.presentation.place.components

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.util.OrderType


data class PlaceState(
    val places:List<Place> = emptyList(),
    val orderType: OrderType = OrderType.Ascending,

)
