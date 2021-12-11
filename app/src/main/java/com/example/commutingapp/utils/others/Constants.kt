package com.example.commutingapp.utils.others



object Constants {
    const val COMMUTER_DATABASE_FILE_NAME = "Commute_DB"

    const val REQUEST_CODE_LOCATION_PERMISSION = 1
    const val ACTION_SHOW_COMMUTER_FRAGMENT = "ACTION_SHOW_COMMUTER_FRAGMENT"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val FACEBOOK_CONNECTION_FAILURE = "CONNECTION_FAILURE: CONNECTION_FAILURE"
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 10000L
    const val NORMAL_LOCATION_UPDATE_INTERVAL = 18000L
    const val TEN_METERS = 10.0f
    const val CAMERA_TILT_DEGREES = 30.00
    const val DEFAULT_CAMERA_ANIMATION_DURATION = 3000
    const val ULTRA_FAST_CAMERA_ANIMATION_DURATION = 1
    const val FAST_CAMERA_ANIMATION_DURATION = 700
    const val CAMERA_ZOOM_MAP_MARKER = 14.80
    const val LAST_KNOWN_LOCATION_MAP_ZOOM = 14.80
    const val DEFAULT_MAP_ZOOM = 4.00
    const val TRACKING_MAP_ZOOM = 16.0
    const val MAX_ZOOM_LEVEL_MAPS = 18.85
    const val MIN_ZOOM_LEVEL_MAPS = 3.00
    const val DEFAULT_LATITUDE = 12.8797
    const val DEFAULT_LONGITUDE = 121.7740

    const val ROUTE_COLOR = "#1297F0"
    const val ROUTE_WIDTH = 7.3f
    const val MAP_MARKER_SIZE = 0.4f
    const val MAP_MARKER_IMAGE_ID = "PIN"
    const val REGEX_NUMBER_VALUE="[0-9]"
    const val REGEX_SPECIAL_CHARACTERS_VALUE = "[!#$%&*()_+=|<>?{}\\[\\]~]"
    @Suppress("Warnings")
    const val REQUEST_CHECK_SETTING = 1001

    const val STARTING_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT = 350
    const val TRACKING_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT = 300
    const val INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT = 0
    const val TIMER_COUNTS: Long = 120000
    const val ONE_SECOND_TO_MILLIS: Long = 1000
    const val REFRESH_EMAIL_SYNCHRONOUSLY_INTERVAL:Long = 800
    const val DELAY_INTERVAL_FOR_NO_INTERNET_DIALOG = 1500
    const val DELAY_INTERVAL_FOR_MAIN_SCREEN_ACTIVITY = 300
    const val DEFAULT_INDICATOR_POSITION = 0
    const val SLIDER_ITEM_COUNTS = 4
    const val USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS = 8
    const val SIGN_UP_LOADING_INTERVAL:Long = 100
    const val KEY_NAME_MAPS_TYPE_SHARED_PREFERENCE = "MapsType"
    const val KEY_NAME_MAPS_3D_SHARED_PREFERENCE = "Maps3D"
    const val KEY_NAME_MAPS_TRAFFIC_SHARED_PREFERENCE = "MapsTraffic"
    const val KEY_NAME_INTRO_SLIDER_SHARED_PREFERENCE = "IntroSlider_StateOfSlides"
    const val KEY_NAME_SWITCH_SATELLITE_SHARED_PREFERENCE = "StateOfSatelliteSwitchButton"
    const val KEY_NAME_SWITCH_TRAFFIC_SHARED_PREFERENCE = "StateOfTrafficSwitchButton"
    const val KEY_NAME_NAVIGATION_MAP_STYLE = "MapStyleNavigation"
    const val REQUEST_CODE_AUTOCOMPLETE = 1
    const val ON_SEARCH_SOURCE_ID = "geojsonSourceLayerId"
    const val ON_MAP_CLICK_SOURCE_ID = "icon-sourceID";
    const val ON_MAP_CLICK_LAYER_ID = "icon-layerID";
    const val ON_SEARCH_LAYER_ID = "symbol-layer-id"
    const val ROUTE_SOURCE_ID = "route-sourceId"
    const val ROUTE_LAYER_ID = "route-layerId"
    const val KEY_DESTINATION_LATITUDE = "destinationLat"
    const val KEY_DESTINATION_LONGITUDE = "destinationLng"
    const val KEY_LAST_LOCATION_LATITUDE = "lastLocationLat"
    const val KEY_LAST_LOCATION_LONGITUDE = "lastLocationLng"
    const val NAVIGATION_ROUTE_LAYER_ID = "road-label"
    const val ROUTE_COLOR_ROAD_CLOSURE = "#5A5A5A"
    const val ROUTE_COLOR_ROAD_RESTRICTED = "#FF6A02"
    const val ROUTE_COLOR_HEAVY_CONGESTION = "#FF0E0E"
    const val ROUTE_COLOR_SEVERE_CONGESTION = "#FCBD8C"
    const val ROUTE_COLOR_MODERATE_CONGESTION = "#FCF08C"
    const val ROUTE_COLOR_LOW_CONGESTION = "#9AFF54"

}