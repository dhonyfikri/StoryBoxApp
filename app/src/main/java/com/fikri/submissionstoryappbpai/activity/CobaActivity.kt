package com.fikri.submissionstoryappbpai.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fikri.submissionstoryappbpai.databinding.ActivityCobaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class CobaActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val MAPVIEW_BUNDLE_KEY = "my_maps"
    }

    private lateinit var binding: ActivityCobaBinding

    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCobaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mMapView = binding.mapsKu
        initGoogleMap(savedInstanceState)
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        binding.btn.setOnClickListener {
            val kebabBudheSpace = LatLng(-6.342958179, 106.929503828)
            mMap.addMarker(
                MarkerOptions().position(kebabBudheSpace).title("Kebab Budhe")
                    .snippet("Kebab uwenak polll")
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kebabBudheSpace, 20f))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}