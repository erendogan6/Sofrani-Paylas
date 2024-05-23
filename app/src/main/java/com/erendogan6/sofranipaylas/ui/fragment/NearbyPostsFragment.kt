package com.erendogan6.sofranipaylas.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.erendogan6.sofranipaylas.R
import com.erendogan6.sofranipaylas.viewmodel.NearbyPostsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NearbyPostsFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: NearbyPostsViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var googleMap: GoogleMap
    private var userMarker: Marker? = null
    private var isFirstLocationUpdate = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_nearby_posts, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocationCallback()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        getLocationAndFetchPosts()
        googleMap.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            showMarkerOptionsDialog(marker)
            true
        }
        enableMyLocation()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    if (isFirstLocationUpdate) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                        isFirstLocationUpdate = false
                    }
                    updateLocationMarker(userLocation)
                    fetchNearbyPosts(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun getLocationAndFetchPosts() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).setWaitForAccurateLocation(false).setMinUpdateIntervalMillis(5000).setMaxUpdateDelayMillis(15000).build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun fetchNearbyPosts(latitude: Double, longitude: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.fetchNearbyPosts(latitude, longitude)
            viewModel.posts.observe(viewLifecycleOwner) { posts ->
                googleMap.clear()
                enableMyLocation()
                for (post in posts) {
                    val postLocation = LatLng(post.latitude, post.longitude)
                    googleMap.addMarker(MarkerOptions().position(postLocation).title(post.title).snippet(post.description))
                }
            }
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun updateLocationMarker(location: LatLng) {
        if (userMarker == null) {
            userMarker = googleMap.addMarker(MarkerOptions().position(location).title("Mevcut Konum"))
        } else {
            userMarker?.position = location
        }
    }

    private fun showMarkerOptionsDialog(marker: Marker) {
        AlertDialog.Builder(requireContext()).setTitle(marker.title).setMessage(marker.snippet).setPositiveButton("Yol Tarifi") { _, _ ->
            val uri = "http://maps.google.com/maps?daddr=${marker.position.latitude},${marker.position.longitude}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        }.setNegativeButton("Detayları Gör") { _, _ ->
            val post = viewModel.posts.value?.find { it.title == marker.title }
            post?.let {
                val action = NearbyPostsFragmentDirections.actionNearbyPostsFragmentToPostDetailFragment(it.postID)
                findNavController().navigate(action)
            }
        }.setNeutralButton("Kapat") { dialog, _ ->
            dialog.dismiss()
        }.show()
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
