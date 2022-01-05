package com.example.commutingapp.feature_note.domain.use_case

import com.example.commutingapp.feature_note.domain.model.Place
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository

class GetPlaceByName (
    private val repository: PlaceRepository){

    suspend operator fun invoke(placeName:String):Place{
        return repository.getPlaceListByName(placeName)
    }

}