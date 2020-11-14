package com.jintin.fancylocation.livedata

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*

class LocationLiveData(
    private val context: Context,
    private val locationRequest: LocationRequest
) : LiveData<LocationData>() {

    private val locationProvider by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationListener by lazy {
        LocationListener()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun onActive() {
        super.onActive()
        requestLocationUpdates()
    }

    override fun onInactive() {
        super.onInactive()
        removeLocationUpdates()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun observe(owner: LifecycleOwner, observer: Observer<in LocationData>) {
        super.observe(owner, observer)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun observeForever(observer: Observer<in LocationData>) {
        super.observeForever(observer)
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
                value = LocationData.Success(it)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onLocationAvailability(availability: LocationAvailability?) {
            if (availability?.isLocationAvailable == false) {
                value = LocationData.Fail
            }
        }
    }

}