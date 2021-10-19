package com.example.commutingapp.data.others

import android.graphics.Color


object Constants {
    const val COMMUTER_DATABASE_FILE_NAME = "Commute_DB"

    const val REQUEST_CODE_LOCATION_PERMISSION = 1

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_COMMUTER_FRAGMENT = "ACTION_SHOW_COMMUTER_FRAGMENT"
    const val REQUEST_CODE_PAUSE = 1
    const val REQUEST_CODE_RESUME = 2
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
    const val FACEBOOK_CONNECTION_FAILURE = "CONNECTION_FAILURE: CONNECTION_FAILURE"

    const val FASTEST_LOCATION_UPDATE_INTERVAL = 12000L
    const val NORMAL_LOCATION_UPDATE_INTERVAL = 18000L
    const val TRACKING_MAP_ZOOM = 16.0
    const val TEN_METERS = 10.0f
    const val CAMERA_TILT_DEGREES = 30.00
    const val CAMERA_ZOOM_MAP_MARKER = 14.00
    const val LAST_KNOWN_LOCATION_MAP_ZOOM = 14.80
    const val DEFAULT_MAP_ZOOM = 4.00
    const val MINIMUM_MAP_LEVEL = 18.85
    const val DEFAULT_LATITUDE = 12.8797
    const val DEFAULT_LONGITUDE = 121.7740
    const val POLYLINE_COLOR = Color.BLUE
    const val POLYLINE_WIDTH = 5.0f
    const val MAP_MARKER_SIZE = 2.0f
    const val MAP_MARKER_IMAGE_NAME = "PIN"
    const val REGEX_NUMBER_VALUE="[0-9]"
    const val REGEX_SPECIAL_CHARACTERS_VALUE = "[!#$%&*()_+=|<>?{}\\[\\]~]"
    @Suppress("Warnings")
    const val MAP_STYLE = "mapbox://styles/johndominic/ckujw0oi96ns517ql91zs7j4g"
    const val REQUEST_CHECK_SETTING = 1001
    const val STOPWATCH_INTERVAL = 50L
    const val ONE_SECOND = 1000L
}