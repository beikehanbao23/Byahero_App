package com.example.commutingapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.commutingapp.data.local_db.CommuterDatabase
import com.example.commutingapp.data.others.Constants.COMMUTER_DATABASE_FILE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Singleton
    @Provides
    fun provideCommuterDatabase(
     @ApplicationContext app: Context,
    ) = Room.databaseBuilder(app,
        CommuterDatabase::class.java,
        COMMUTER_DATABASE_FILE_NAME).build()


    @Singleton
    @Provides
    fun providerRunDao(db:CommuterDatabase) = db.getCommuterDao()
}