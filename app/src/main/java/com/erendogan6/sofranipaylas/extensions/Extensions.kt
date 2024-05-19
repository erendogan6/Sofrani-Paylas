package com.erendogan6.sofranipaylas.extensions

import android.content.Intent
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