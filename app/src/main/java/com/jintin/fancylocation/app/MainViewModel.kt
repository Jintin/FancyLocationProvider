package com.jintin.fancylocation.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.jintin.fancylocation.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(3000)
            .build()

//    @ExperimentalCoroutinesApi
//    val locationFlow = LocationFlow(application, locationRequest)
//    val locationStateFlow = LocationFlow(application, locationRequest)
//    val locationLiveData = LocationLiveData(application, locationRequest)

    // we can also provide custom vendor by create your own LocationProvider
    private val locationProvider: ILocationProvider = LocationProvider(application, locationRequest)

    @ExperimentalCoroutinesApi
    val locationFlow = locationProvider.asFlow()
    val locationLiveData = locationProvider.asLiveData()
    val locationStateFlow = locationProvider.asStateFlow()
}