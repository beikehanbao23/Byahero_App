package com.example.commutingapp.feature_note.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.commutingapp.feature_note.domain.model.Place


@Database(
    entities = [Place::class],
    version = 1
)
abstract class PlaceDatabase:RoomDatabase() {

    abstract val placeDao:PlaceDao
}