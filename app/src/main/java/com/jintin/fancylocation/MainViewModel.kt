package com.jintin.fancylocation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationRequest
import com.jintin.fancylocation.livedata.LocationFlow
import com.jintin.fancylocation.livedata.LocationLiveData
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
}