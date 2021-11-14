package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.properties

import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.ON_SEARCH_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ON_SEARCH_SOURCE_ID

class OnSearchMapMarkerProperties :MapLayerProperties{
    override fun getSourceId(): String = ON_SEARCH_SOURCE_ID
    override fun getLayerId(): String = ON_SEARCH_LAYER_ID
    override fun getImageId(): String = MAP_MARKER_IMAGE_ID

}