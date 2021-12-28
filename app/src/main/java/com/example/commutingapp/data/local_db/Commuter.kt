package com.example.commutingapp.data.local_db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Commuter_Table")
data class Commuter(
    var image: Bitmap? = null,
    var timestamp: Long = 0L,
    var averageSpeed_KmH: Float = 0f,
    var distanceInMeters:Int = 0,
    var timeInMillis: Long = 0L,
    var wentPlaces:String? = null) {

@PrimaryKey(autoGenerate = true)
var id: Int? = null


}