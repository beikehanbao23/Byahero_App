package com.example.commutingapp.feature_note.data.di

import android.app.Application
import androidx.room.Room
import com.example.commutingapp.feature_note.data.data_source.PlaceDatabase
import com.example.commutingapp.feature_note.data.repository.PlaceRepositoryImpl
import com.example.commutingapp.feature_note.domain.repository.PlaceRepository
import com.example.commutingapp.feature_note.domain.use_case.DeletePlace
import com.example.commutingapp.feature_note.domain.use_case.GetPlace
import com.example.commutingapp.feature_note.domain.use_case.PlaceUseCase
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
    fun providePlaceDatabase(app:Application):PlaceDatabase{
        return Room.databaseBuilder(
            app,
            PlaceDatabase::class.java,
            PlaceDatabase.database_name
        ).build()
    }


    @Provides
    @Singleton
    fun providePlaceRepository(db:PlaceDatabase):PlaceRepository{
        return PlaceRepositoryImpl(db.placeDao)
    }


    @Provides
    @Singleton
    fun providePlaceUseCase(repository: PlaceRepository):PlaceUseCase{
        return PlaceUseCase(
            deletePlace = DeletePlace(repository),
            getPlace = GetPlace(repository)
        )
    }



}