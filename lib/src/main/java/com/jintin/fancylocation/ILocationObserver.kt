package com.jintin.fancylocation

import android.location.Location

interface ILocationObserver {
    fun onLocationResult(location: Location)
    fun onLocationFailed()
}