package com.example.commutingapp.feature_note.presentation.place.components

import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.domain.util.OrderType

sealed class PlaceBookmarksEvent {
    data class ChangeOrder(val orderType: OrderType):PlaceBookmarksEvent()
    data class DeletePlaceBookmarks(val placeBookmarks:PlaceBookmarks):PlaceBookmarksEvent()
    object RestoreNote:PlaceBookmarksEvent()
    data class SearchPlaceBookmarks(val placeName:String):PlaceBookmarksEvent()
}