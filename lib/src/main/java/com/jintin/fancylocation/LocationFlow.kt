package com.jintin.fancylocation

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runBlocking

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
        locationProvider.requestLocationUpdates(object : ILocationObserver {
            override fun onLocationResult(location: Location) {
                setValue(channel, LocationData.Success(location))
            }

            override fun onLocationFailed() {
                setValue(channel, LocationData.Fail)
            }

        })
        awaitClose {
            locationProvider.removeLocationUpdates()
        }
    }

    private fun setValue(sendChannel: SendChannel<LocationData>, value: LocationData) =
        runBlocking {
            sendChannel.send(value)
        }
}