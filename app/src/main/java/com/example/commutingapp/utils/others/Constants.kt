package com.example.commutingapp.utils.others

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
    const val TEN_METERS = 10.0f
    const val CAMERA_TILT_DEGREES = 30.00
    const val DEFAULT_CAMERA_ANIMATION_DURATION = 3000
    const val ULTRA_FAST_CAMERA_ANIMATION_DURATION = 1
    const val FAST_CAMERA_ANIMATION_DURATION = 800
    const val CAMERA_ZOOM_MAP_MARKER = 14.80
    const val LAST_KNOWN_LOCATION_MAP_ZOOM = 14.80
    const val DEFAULT_MAP_ZOOM = 4.00
    const val TRACKING_MAP_ZOOM = 16.0
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
    const val REQUEST_CHECK_SETTING = 1001
    const val STOPWATCH_INTERVAL = 50L
    const val ONE_SECOND = 1000L
    const val VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT = 380
    const val INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT = 0
    const val TIMER_COUNTS: Long = 120000
    const val ONE_SECOND_TO_MILLIS: Long = 1000
    const val REFRESH_EMAIL_SYNCHRONOUSLY_INTERVAL:Long = 2200
    const val DEFAULT_INDICATOR_POSITION = 0
    const val SLIDER_ITEM_COUNTS = 4
    const val USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS = 8
    const val SIGN_UP_LOADING_INTERVAL:Long = 100
    const val FILE_NAME_INTRO_SLIDER_SHARED_PREFERENCE = "IntroSlider"
    const val FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE = "MapsType"
    const val KEY_NAME_INTRO_SLIDER_SHARED_PREFERENCE = "IntroSlider_StateOfSlides"
}