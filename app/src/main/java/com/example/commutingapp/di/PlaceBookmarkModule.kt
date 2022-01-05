package com.example.commutingapp.di

import android.app.Application
import androidx.room.Room
import com.example.commutingapp.feature_note.data.data_source.PlaceBookmarksDatabase
import com.example.commutingapp.feature_note.data.repository.PlaceBookmarksRepositoryImpl
import com.example.commutingapp.feature_note.domain.repository.PlaceBookmarksRepository
import com.example.commutingapp.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PlaceBookmarkModule {

    @Provides
    @Singleton
    fun providePlaceDatabase(app:Application):PlaceBookmarksDatabase{
        return Room.databaseBuilder(
            app,
            PlaceBookmarksDatabase::class.java,
            PlaceBookmarksDatabase.database_name
        ).build()
    }


    @Provides
    @Singleton
    fun providePlaceRepository(db:PlaceBookmarksDatabase):PlaceBookmarksRepository{
        return PlaceBookmarksRepositoryImpl(db.placeBookmarksDao)
    }


    @Provides
    @Singleton
    fun providePlaceUseCase(bookmarksRepository: PlaceBookmarksRepository):PlaceBookmarksUseCase{
        return PlaceBookmarksUseCase(
            deletePlaceFromBookmarks = DeletePlaceFromBookmarks(bookmarksRepository),
            getPlaceFromBookmarks = GetPlacesFromBookmarks(bookmarksRepository),
            addPlaceToBookmarks = AddPlaceToBookmarks(bookmarksRepository),
            getPlaceNameFromBookmarks = GetPlaceByNameFromBookmarks(bookmarksRepository)
        )
    }



}