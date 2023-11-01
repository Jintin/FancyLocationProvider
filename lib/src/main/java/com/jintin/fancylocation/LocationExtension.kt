package com.jintin.fancylocation

fun ILocationProvider.asLiveData(): LocationLiveData {
    return LocationLiveData(this)
}

fun ILocationProvider.asFlow(): LocationFlow {
    return LocationFlow(this)
}

fun ILocationProvider.asStateFlow(): LocationStateFlow {
    return LocationStateFlow(this)
}