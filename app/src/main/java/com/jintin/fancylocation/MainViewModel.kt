package com.jintin.fancylocation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRequest =
        LocationRequest.create()
            .setInterval(3000)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)


    @ExperimentalCoroutinesApi
    val locationFlow = LocationFlow(application, locationRequest)
    val locationLiveData = LocationLiveData(application, locationRequest)

//    // we can also provide custom vendor by create your own LocationProvider
//    private val locationProvider: ILocationProvider = LocationProvider(application, locationRequest)
//
//    @ExperimentalCoroutinesApi
//    val locationFlow = locationProvider.asFlow()
//    val locationLiveData = locationProvider.asLiveData()


}