package com.jintin.fancylocation

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

@ExperimentalCoroutinesApi
class LocationFlow(
    private val locationProvider: ILocationProvider
) {

    constructor(
        context: Context,
        locationRequest: LocationRequest
    ) : this(LocationProvider(context, locationRequest))

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun get(): Flow<LocationData> = channelFlow {
        channel.trySendBlocking(LocationData.Loading)
        locationProvider.requestLocationUpdates(object : ILocationObserver {
            override fun onLocationResult(location: Location) {
                channel.trySendBlocking(LocationData.Success(location))
            }

            override fun onLocationFailed() {
                channel.trySendBlocking(LocationData.Fail)
            }
        })
        awaitClose {
            locationProvider.removeLocationUpdates()
        }
    }
}