package com.example.commutingapp.views.ui.subComponents

import android.view.View
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
import com.example.commutingapp.utils.others.Constants.TRACKING_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
import com.google.android.material.bottomsheet.BottomSheetBehavior

class TrackingBottomSheet(view:View):IComponent {
    private val trackingBottomSheet: BottomSheetBehavior<View>  = BottomSheetBehavior.from(view.findViewById(
        R.id.bottomSheetTrackingState)).apply {
        state = BottomSheetBehavior.STATE_COLLAPSED
        isHideable=false

    }
    override fun show() {
        trackingBottomSheet.peekHeight = TRACKING_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }

    override fun hide() {
        trackingBottomSheet.peekHeight = INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }
}