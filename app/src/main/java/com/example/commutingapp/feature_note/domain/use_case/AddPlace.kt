package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.InvalidPlaceException
import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository

data class AddPlace(
    private val repository: PlaceRepository) {

    suspend operator fun invoke(place:Place){

        if(place.placeName.isBlank() || place.placeText.isBlank()){
            throw InvalidPlaceException("Insertion Failed, place name or place text is invalid")
        }
        repository.insertPlace(place)
    }
}