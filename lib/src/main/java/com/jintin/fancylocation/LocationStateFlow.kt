package com.jintin.fancylocation

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.coroutineContext

class LocationStateFlow(
    private val locationProvider: ILocationProvider
) {
    private val _value = MutableStateFlow<LocationData>(LocationData.Init)
    val value: StateFlow<LocationData> get() = _value

    constructor(
        context: Context,
        locationRequest: LocationRequest
    ) : this(LocationProvider(context, locationRequest))

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    suspend fun start() {
        _value.value = LocationData.Loading
        locationProvider.requestLocationUpdates(object : ILocationObserver {
            override fun onLocationResult(location: Location) {
                _value.value = LocationData.Success(location)
            }

            override fun onLocationFailed() {
                _value.value = LocationData.Fail
            }
        })
        with(CoroutineScope(coroutineContext)) { // spawns and uses parent scope!
            launch {
                suspendCancellableCoroutine<Unit> {
                    it.invokeOnCancellation {
                        locationProvider.removeLocationUpdates()
                    }
                }
            }
        }
    }
}