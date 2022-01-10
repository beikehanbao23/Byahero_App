package com.example.commutingapp.feature_note.presentation.place.components


/**
 * Ui event for result from state interaction
 */
sealed class PlaceBookmarksUiEvent {

    data class ShowSnackBar(val message:String):PlaceBookmarksUiEvent()
    object SavePlace:PlaceBookmarksUiEvent()

}