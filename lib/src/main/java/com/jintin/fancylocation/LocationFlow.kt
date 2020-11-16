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
) : ILocationObserver {

    constructor(
        context: Context,
        locationRequest: LocationRequest
    ) : this(LocationProvider(context, locationRequest))

    private var sendChannel: SendChannel<LocationData>? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun get(): Flow<LocationData> = channelFlow {
        locationProvider.requestLocationUpdates(this@LocationFlow)
        sendChannel = this.channel
        awaitClose {
            sendChannel = null
            locationProvider.removeLocationUpdates()
        }
    }

    private fun setValue(value: LocationData) = runBlocking {
        sendChannel?.send(value)
    }

    override fun onLocationResult(location: Location) {
        setValue(LocationData.Success(location))
    }

    override fun onLocationFailed() {
        setValue(LocationData.Fail)
    }
}