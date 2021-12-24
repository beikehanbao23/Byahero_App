package com.example.commutingapp.data.api

data class AirQuality(
    var co: Double,
    var gb_defra_index: Int,
    var no2: Double,
    var o3: Double,
    var pm10: Double,
    var pm2_5: Double,
    var so2: Double,
    var us_epa_index: Int
)