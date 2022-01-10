package com.example.commutingapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.commutingapp.utils.others.Constants

@Entity
data class PlaceBookmarks(
    @PrimaryKey(autoGenerate = false)
    val placeName:String = "Philippines",
    val longitude:Double = Constants.DEFAULT_LONGITUDE,
    val latitude:Double = Constants.DEFAULT_LATITUDE,
    val placeText:String = ""
)


