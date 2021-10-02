package com.example.commutingapp.data.others

import android.graphics.Color
import com.google.firebase.database.collection.LLRBNode

object Constants {
    const val COMMUTER_DATABASE_FILE_NAME = "Commute_DB"

    const val REQUEST_CODE_LOCATION_PERMISSION = 1

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_COMMUTER_FRAGMENT = "ACTION_SHOW_COMMUTER_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
    const val FACEBOOK_CONNECTION_FAILURE = "CONNECTION_FAILURE: CONNECTION_FAILURE"

    const val FASTEST_LOCATION_UPDATE_INTERVAL = 8000L
    const val NORMAL_LOCATION_UPDATE_INTERVAL = 18000L
    const val DEFAULT_MAP_ZOOM = 15f
    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 11.5f
    const val REGEX_NUMBER_VALUE="[0-9]"
    const val REGEX_SPECIAL_CHARACTERS_VALUE = "[!#$%&*()_+=|<>?{}\\[\\]~]"
}