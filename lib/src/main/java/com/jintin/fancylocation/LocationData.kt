package com.jintin.fancylocation

import android.location.Location

sealed class LocationData {
    data class Success(val location: Location) : LocationData()
    object Fail : LocationData()
}