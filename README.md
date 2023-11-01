# FancyLocationProvider

[![CircleCI](https://circleci.com/gh/Jintin/FancyLocationProvider.svg?style=shield)](https://circleci.com/gh/Jintin/FancyLocationProvider)
[![jitpack](https://jitpack.io/v/Jintin/FancyLocationProvider.svg)](https://jitpack.io/#Jintin/FancyLocationProvider)

Wrapper of FusedLocationProviderClient for Android to support modern usage like LiveData or Flow.

## Install

Add [Jitpack](https://jitpack.io/) repository to your root `build.grable`:
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Then add dependency in your module `build.gradle`:
```groovy
dependencies {
  implementation 'com.github.jintin:FancyLocationProvider:2.0.0'
}
```

## Usage

### LiveData
Here is an example of how you can create a `LocationLiveData` in `ViewModel` layer, just provide `Context` and the `LocationRequest` with your own config.
```kotlin
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRequest =
        LocationRequest.create()
            .setInterval(3000)
            .setFastestInterval(3000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    val locationLiveData = LocationLiveData(application, locationRequest)
}
```
And here is how you observe the update on `View` layer.
```kotlin
// check for the location permission first
locationLiveData.observe(this) {
    when (it) {
        is LocationData.Success -> // get location by "it.location"
        is LocationData.Fail -> // Fail to get location
    }
}

```

### Flow

```kotlin
val locationFlow = LocationFlow(application, locationRequest)

// check for the location permission first
// should inside coroutine
locationFlow.get().collect {
    when (it) {
        is LocationData.Success -> // get location by "it.location"
        is LocationData.Fail -> // Fail to get location
    }
}

```

You can go to ./app module for more information.

## Custom Location Provider
FancyLocationProvider using GMS as the default location provider as it serve the most use case.
But you can also used it with any other provider you want like Huawei HMS. Just create the custom provider implement the `ILocationProvider` interface like this:
```kotlin
class LocationProvider(
    private val context: Context,
    private val locationRequest: LocationRequest
) : ILocationProvider {

    private val locationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationListener by lazy {
        LocationListener()
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun requestLocationUpdates(locationObserver: ILocationObserver) {
        locationListener.locationObserver = locationObserver
        locationProviderClient.requestLocationUpdates(
            locationRequest,
            locationListener,
            Looper.getMainLooper()
        )
    }

    override fun removeLocationUpdates() {
        locationProviderClient.removeLocationUpdates(locationListener)
    }

    private class LocationListener : LocationCallback() {
        var locationObserver: ILocationObserver? = null

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
```
Then you can create the custom provider and transform it into LiveData or Flow.
```kotlin
private val locationProvider: ILocationProvider = LocationProvider(context, locationRequest)

val locationFlow: LocationFlow = locationProvider.asFlow()
val locationLiveData: LocationLiveData = locationProvider.asLiveData()
```

## Contributing
Bug reports and pull requests are welcome on GitHub at [https://github.com/Jintin/FancyLocationProvider](https://github.com/Jintin/FancyLocationProvider).

## License
The package is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).

[![Buy Me A Coffee](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/jintin)
