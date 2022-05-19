package com.willyishmael.dicodingstoryapp.view.maps

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.willyishmael.dicodingstoryapp.R
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.data.remote.response.ListStoryItem
import com.willyishmael.dicodingstoryapp.databinding.ActivityMapsBinding
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel

    private var mapIsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
        setupListStory()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mapIsReady = true
        mMap = googleMap

        // Maps Controller
        mMap.uiSettings.isZoomControlsEnabled = true

        val cameraLocation = LatLng(-4.170031622518649, 122.7837540787655)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraLocation, 5f))

    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mapsViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MapsViewModel::class.java]
    }

    private fun setupListStory() {
        mapsViewModel.apply {
            getUserToken().observe(this@MapsActivity) { mToken ->
                getStories(mToken)
            }
            listStories.observe(this@MapsActivity) { listStory ->
                if (mapIsReady) {
                    setStoryLocation(listStory)
                }
            }
            isLoading.observe(this@MapsActivity) { loading ->

            }
        }
    }

    private fun setStoryLocation(listStory: List<ListStoryItem>) {
        for (story in listStory) {
            val position = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(story.name)
                    .snippet(story.description)
            )
        }
    }

}