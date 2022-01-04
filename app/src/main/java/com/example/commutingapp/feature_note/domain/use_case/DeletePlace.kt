package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository

class DeletePlace(
    private val repository: PlaceRepository
) {

    suspend operator fun invoke(place:Place){
        repository.deletePlace(place)
    }

}