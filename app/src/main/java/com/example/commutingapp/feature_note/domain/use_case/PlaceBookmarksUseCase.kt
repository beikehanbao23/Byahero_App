package com.example.commutingapp.feature_note.domain.use_case

class PlaceBookmarksUseCase(
    val deletePlaceFromBookmarks: DeletePlaceFromBookmarks,
    val getPlaceFromBookmarks: GetPlacesFromBookmarks,
    val addPlaceToBookmarks:AddPlaceToBookmarks

)