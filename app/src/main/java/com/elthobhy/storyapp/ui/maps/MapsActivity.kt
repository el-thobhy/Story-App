package com.elthobhy.storyapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.utils.showDialogError
import com.elthobhy.storyapp.core.utils.showDialogLoading
import com.elthobhy.storyapp.core.utils.vo.Status
import com.elthobhy.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.ext.android.inject
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val locationStoryViewModel by inject<LocationStoryViewModel>()
    private lateinit var dialogLoading: AlertDialog
    private lateinit var dialogError: AlertDialog
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoading = showDialogLoading(this)
        dialogError = showDialogError(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        dialogLoading.show()
        getMyLocation()
        addLocationMarker()
        setCustomMapStyle()

    }

    private fun setCustomMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style))
            if (!success) {
                Log.d("MapsActivity", "setMapStyle: Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.d("MapsActivity", "setMapStyle: $e")
        }
    }

    private fun addLocationMarker() {
        locationStoryViewModel.getStories().observe(this) { resource ->
            resource.data?.map { story ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        dialogLoading.dismiss()
                        if (story.lat != null) {
                            val latLng = LatLng(story.lat, story.lon as Double)
                            val addresses = getAddress(story.lat, story.lon)
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(story.name)
                                    .snippet(addresses)
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            boundsBuilder.include(latLng)
                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    100
                                )
                            )
                        } else {
                            dialogLoading.dismiss()
                        }

                    }
                    Status.LOADING -> dialogLoading.show()
                    Status.ERROR -> {
                        dialogLoading.dismiss()
                        dialogError = showDialogError(this, resource.message.toString())
                        dialogError.show()
                        Log.e("mapsActivity", "addLocationMarker: ${resource.message}")
                    }
                }
            }
        }
    }

    private fun getAddress(lat: Double, lon: Double): String? {
        var address: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                address = list[0].getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            getMyLocation()
        }
    }

    override fun onStop() {
        super.onStop()
        dialogLoading.dismiss()
        dialogError.dismiss()
    }
}