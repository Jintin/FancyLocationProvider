package com.jintin.fancylocation.livedata

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
class LocationFlow(
    private val context: Context,
    private val locationRequest: LocationRequest
) {
    private val locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationListener by lazy {
        LocationListener()
    }
    private var scope: ProducerScope<LocationData>? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun get(): Flow<LocationData> = channelFlow {
        requestLocationUpdates()
        try {
            scope = this
            awaitCancellation()
        } finally {
            scope = null
            removeLocationUpdates()
        }
    }

    private fun setValue(value: LocationData) = runBlocking {
        scope?.channel?.send(value)
    }

    private fun removeLocationUpdates() {
        locationProvider.removeLocationUpdates(locationListener)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun requestLocationUpdates() {
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationListener,
            Looper.getMainLooper()
        )
    }

    inner class LocationListener : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.lastLocation?.let {
                setValue(LocationData.Success(it))
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability?) {
            if (availability?.isLocationAvailable == false) {
                setValue(LocationData.Fail)
            }
        }
    }
}