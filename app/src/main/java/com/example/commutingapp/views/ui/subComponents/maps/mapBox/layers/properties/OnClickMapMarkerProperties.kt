package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.properties

import com.example.commutingapp.utils.others.Constants

class OnClickMapMarkerProperties : MapLayerProperties{

    override fun getSourceId():String = Constants.ON_MAP_CLICK_SOURCE_ID
    override fun getLayerId():String = Constants.ON_MAP_CLICK_LAYER_ID
    override fun getImageId():String = Constants.MAP_MARKER_IMAGE_ID
}