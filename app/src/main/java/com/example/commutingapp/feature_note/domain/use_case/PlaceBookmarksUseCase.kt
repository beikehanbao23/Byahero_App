package com.example.commutingapp.feature_note.domain.use_case

data class PlaceBookmarksUseCase(
    val deletePlaceFromBookmarks: DeletePlaceFromBookmarks,
    val getPlaceFromBookmarks: GetPlacesFromBookmarks,
    val addPlaceToBookmarks:AddPlaceToBookmarks

)