package com.example.commutingapp.feature_note.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks


@Database(
    entities = [PlaceBookmarks::class],
    version = 1
)
abstract class PlaceBookmarksDatabase:RoomDatabase() {

    abstract val placeBookmarksDao:PlaceBookmarksDao

    companion object{
        const val database_name = "place_bookmarks_db"
    }
}