package com.example.commutingapp.feature_note.presentation.place.components

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.util.OrderType

/** this use case is for values, this is alternatives for global variables */
data class PlaceBookmarksState(
    val placeBookmarks:List<PlaceBookmarks> = emptyList(),
    val orderType: OrderType = OrderType.Ascending,
    )
