package com.example.commutingapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Commuter::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class CommuterDatabase:RoomDatabase(){

    abstract fun getCommuterDao():CommuterDao
}