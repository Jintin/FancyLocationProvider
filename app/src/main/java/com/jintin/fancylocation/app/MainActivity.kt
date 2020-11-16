package com.jintin.fancylocation.app

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jintin.fancylocation.LocationData
import com.jintin.fancylocation.R
import com.jintin.fancylocation.observeLocationWithPermissionCheck
import com.jintin.fancylocation.onRequestPermissionsResult
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    companion object {
        const val TYPE_LIVEDATA = 0
        const val TYPE_FLOW = 1
    }

    private val mainViewModel by viewModels<MainViewModel>()

    private val type = TYPE_FLOW

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
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun flowObserve() {
        lifecycleScope.launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            mainViewModel.locationFlow.get().collect {
                updateUI(it)
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun liveDataObserve() {
        mainViewModel.locationLiveData.observe(this) {
            updateUI(it)
        }
    }

    private fun updateUI(locationData: LocationData) {
        findViewById<TextView>(R.id.location).text = when (locationData) {
            is LocationData.Success -> locationData.location.toString()
            is LocationData.Fail -> "Fail to get location"
        }
    }
}