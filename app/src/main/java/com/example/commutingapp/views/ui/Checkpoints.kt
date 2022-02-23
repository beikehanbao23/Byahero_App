package com.example.commutingapp.views.ui

sealed class Checkpoints(val distance: IntRange, var hasReachedCheckpoint:Boolean) {
    object DistanceIn1000Meters: Checkpoints(distance = 970..1030, hasReachedCheckpoint = false)
    object DistanceIn500Meters: Checkpoints(distance = 470..530, hasReachedCheckpoint = false)
    object DistanceIn300Meters: Checkpoints(distance = 270..330, hasReachedCheckpoint = false)
    object DistanceIn150Meters: Checkpoints(distance = 110..180, hasReachedCheckpoint = false)
    object DistanceIn50Meters: Checkpoints(distance = 10..80, hasReachedCheckpoint = false)
}
