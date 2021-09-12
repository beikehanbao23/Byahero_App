package com.example.commutingapp.data.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Commute_Table")
data class Run(
    var image: Bitmap? = null,
    var timestamp: Long = 0L,
    var averageSpeed_KmH: Float = 0f,
    var distanceInMeters:Int = 0,
    var timeSpent: Long = 0L
) {

@PrimaryKey(autoGenerate = true)
var ID: Int? = null


}