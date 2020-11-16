package com.jintin.fancylocation

import android.Manifest
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*

class LocationProvider(
    private val context: Context,
    private val locationRequest: LocationRequest
) : ILocationProvider {

    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationListener by lazy {
        LocationListener()
    }
    private var locationObserver: ILocationObserver? = null

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun requestLocationUpdates(locationObserver: ILocationObserver) {
        this.locationObserver = locationObserver
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationListener,
            Looper.getMainLooper()
        )
    }

    override fun removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationListener)
    }

    inner class LocationListener : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.lastLocation?.let {
                locationObserver?.onLocationResult(it)
            }
        }

        override fun onLocationAvailability(availability: LocationAvailability?) {
            if (availability?.isLocationAvailable == false) {
                locationObserver?.onLocationFailed()
            }
        }
    }
}