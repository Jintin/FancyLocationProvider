package com.jintin.fancylocation

import android.location.Location

sealed class LocationData {
    data object Init : LocationData()
    data object Loading : LocationData()
    data class Success(val location: Location) : LocationData()
    data object Fail : LocationData()
}