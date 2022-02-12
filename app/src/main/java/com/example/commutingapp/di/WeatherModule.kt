package com.example.commutingapp.di

import com.example.commutingapp.data.model.WeatherServiceAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {
    @Provides
    @Singleton
    fun provideWeatherRetrofit(): WeatherServiceAPI = Retrofit.Builder()
        .baseUrl("http://api.weatherapi.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherServiceAPI::class.java)



}