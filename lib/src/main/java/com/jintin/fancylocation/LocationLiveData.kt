package com.jintin.fancylocation

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationRequest

class LocationLiveData(
    private val locationProvider: ILocationProvider
) : LiveData<LocationData>() {

    private val locationObserver = LocationObserver()

    constructor(
        context: Context,
        locationRequest: LocationRequest
    ) : this(LocationProvider(context, locationRequest))

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onActive() {
        super.onActive()
        locationProvider.requestLocationUpdates(locationObserver)
    }

    override fun onInactive() {
        super.onInactive()
        locationProvider.removeLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in LocationData>
    ) {
        super.observe(owner, observer)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun observeForever(observer: Observer<in LocationData>) {
        super.observeForever(observer)
    }

    private inner class LocationObserver : ILocationObserver {
        override fun onLocationResult(location: Location) {
            value = LocationData.Success(location)
        }

        override fun onLocationFailed() {
            value = LocationData.Fail
        }
    }
}