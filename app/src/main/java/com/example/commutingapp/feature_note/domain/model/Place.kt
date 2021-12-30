package com.example.commutingapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.type.LatLng

@Entity
data class Place(
    @PrimaryKey(autoGenerate = false)
    val placeName:String,
    val location: LatLng,
    val placeText:String
)
