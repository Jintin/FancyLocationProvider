package com.jintin.fancylocation.app

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jintin.fancylocation.LocationData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    companion object {
        const val TYPE_LIVEDATA = 0
        const val TYPE_FLOW = 1
        const val TYPE_STATEFLOW = 2
    }

    private val viewModel by viewModels<MainViewModel>()

    private val type = TYPE_STATEFLOW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observeLocationWithPermissionCheck()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) = onRequestPermissionsResult(requestCode, grantResults)

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun observeLocation() {
        when (type) {
            TYPE_LIVEDATA -> liveDataObserve()
            TYPE_FLOW -> flowObserve()
            TYPE_STATEFLOW -> stateFlowObserve()
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun flowObserve() {
        lifecycleScope.launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            viewModel.locationFlow.get().collect(::updateUI)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun stateFlowObserve() {
        lifecycleScope.launch {
            viewModel.locationStateFlow.start()
            viewModel.locationStateFlow.value.collect(::updateUI)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun liveDataObserve() {
        viewModel.locationLiveData.observe(this, ::updateUI)
    }

    private fun updateUI(locationData: LocationData) {
        findViewById<TextView>(R.id.location).text = when (locationData) {
            LocationData.Init -> "Init state"
            LocationData.Loading -> "Start loading"
            LocationData.Fail -> "Fail to get location"
            is LocationData.Success -> locationData.location.toString()
        }
    }
}