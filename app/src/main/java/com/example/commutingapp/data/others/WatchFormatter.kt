package com.example.commutingapp.data.others

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

object WatchFormatter {
    private val numberFormat: NumberFormat = DecimalFormat("00")
    fun getFormattedStopWatchTime(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${numberFormat.format(hours)}:${numberFormat.format(minutes)}:${numberFormat.format(seconds)}"
    }


}