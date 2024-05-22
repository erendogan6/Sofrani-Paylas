package com.erendogan6.sofranipaylas.extensions

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.fragment.app.Fragment
import com.erendogan6.sofranipaylas.ui.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth

fun Fragment.checkUserSessionAndNavigate() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (currentUser == null) {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}

@Suppress("DEPRECATION")
fun Geocoder.getAddress(latitude: Double, longitude: Double, address: (Address?) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
        return
    }

    try {
        address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
    } catch (e: Exception) {
        address(null)
    }
}