package com.example.commutingapp.data.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.views.ui.activities.MainScreen
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClass(@ApplicationContext app: Context):FusedLocationProviderClient=
        FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app:Context):PendingIntent= PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainScreen::class.java).also {
            it.action = Constants.ACTION_SHOW_COMMUTER_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun provideTrackingNotification(@ApplicationContext app: Context,pendingIntent:PendingIntent):NotificationCompat.Builder{
        return NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_jeep)
            .setContentTitle("Commuting")
            .setContentText("Recording your commute.")
            .setContentIntent(pendingIntent)
    }

}