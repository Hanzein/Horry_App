package com.farhanadi.horryapp.user_ui_page.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.ViewModelFactory
import com.farhanadi.horryapp.databinding.FragmentMapBinding
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null

    private val binding get() = _binding!!
    private lateinit var preferences: UserManager
    private lateinit var mMap: GoogleMap
    private lateinit var factory: ViewModelFactory
    private val viewModel: MapViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //setupView()
        setupProperty()
        setupAction()
        getStoryMap()

        return root
    }

    /*private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }*/

    private fun setupProperty() {
        factory = ViewModelFactory.getInstance(requireActivity())
        preferences = UserManager(requireActivity())
    }

    private fun getStoryMap() {
        val token = "Bearer ${preferences.getToken()}"
        viewModel.getWithLocation(1, token).observe(requireActivity()) { response ->
            when (response) {
                is ResultResource.Loading -> {
                    Log.e(TAG, "Loading..")
                }
                is ResultResource.Success -> {
                    showMarker(response.data.listStory)
                }
                is ResultResource.Error -> {
                }
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment)
        if (mapFragment is SupportMapFragment) {
            mapFragment.getMapAsync(this)
        } else {
            Log.e(TAG, "Fragment with ID R.id.ini_fragment_map is not a SupportMapFragment")
        }
    }

    private fun showMarker(listStory: List<ListStoryItem>) {
        for (story in listStory) {
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .snippet(story.description)
                    .title(story.name)
            )
        }
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireActivity(), R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                enableMyLocation()
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        configureMap()
    }

    private fun configureMap() {
        mMap.uiSettings.isZoomControlsEnabled = false
        enableMyLocation()
        setMapStyle()

        // Find your buttons in the binding
        val btnShowMapOptions = binding.root.findViewById<Button>(R.id.btnShowMapOptions)
        val btnZoomIn = binding.root.findViewById<Button>(R.id.btnZoomIn)
        val btnZoomOut = binding.root.findViewById<Button>(R.id.btnZoomOut)
        val btnTerrain = binding.root.findViewById<ImageButton>(R.id.btnTerrain)
        val btnHybrid = binding.root.findViewById<ImageButton>(R.id.btnHybrid)
        val btnDefault = binding.root.findViewById<ImageButton>(R.id.btnDefault)
        val btnSatellite = binding.root.findViewById<ImageButton>(R.id.btnSatellite)

        // Set click listeners for zoom in and zoom out
        btnZoomIn.setOnClickListener { zoomIn() }
        btnZoomOut.setOnClickListener { zoomOut() }

        // Set up map type buttons
        btnShowMapOptions.setOnClickListener {
            // Toggle visibility of map options buttons
            btnTerrain.visibility =
                if (btnTerrain.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            btnHybrid.visibility =
                if (btnHybrid.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            btnDefault.visibility =
                if (btnDefault.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            btnSatellite.visibility =
                if (btnSatellite.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        btnTerrain.setOnClickListener { changeMapType(GoogleMap.MAP_TYPE_TERRAIN) }
        btnHybrid.setOnClickListener { changeMapType(GoogleMap.MAP_TYPE_HYBRID) }
        btnDefault.setOnClickListener { changeMapType(GoogleMap.MAP_TYPE_NORMAL) }
        btnSatellite.setOnClickListener { changeMapType(GoogleMap.MAP_TYPE_SATELLITE) }
    }

    private fun zoomIn() {
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
    }

    private fun zoomOut() {
        mMap.animateCamera(CameraUpdateFactory.zoomOut())
    }

    private fun changeMapType(mapType: Int) {
        mMap.mapType = mapType
    }

    companion object {
        private val TAG = MapFragment::class.simpleName
    }

}