package com.jintin.fancylocation

import android.Manifest
import androidx.annotation.RequiresPermission

interface ILocationProvider {
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun requestLocationUpdates(locationObserver: ILocationObserver)
    fun removeLocationUpdates()
}