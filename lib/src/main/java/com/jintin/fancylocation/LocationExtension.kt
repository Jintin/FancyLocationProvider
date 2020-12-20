package com.jintin.fancylocation

import kotlinx.coroutines.ExperimentalCoroutinesApi

fun ILocationProvider.asLiveData(): LocationLiveData {
    return LocationLiveData(this)
}

@ExperimentalCoroutinesApi
fun ILocationProvider.asFlow(): LocationFlow {
    return LocationFlow(this)
}

fun ILocationProvider.asStateFlow(): LocationStateFlow {
    return LocationStateFlow(this)
}