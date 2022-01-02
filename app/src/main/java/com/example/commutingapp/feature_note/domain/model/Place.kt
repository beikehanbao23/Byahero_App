package com.example.commutingapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Place(
    @PrimaryKey(autoGenerate = false)
    val placeName:String = "Philippines",
    val placeText:String = ""
)
//todo add location param
