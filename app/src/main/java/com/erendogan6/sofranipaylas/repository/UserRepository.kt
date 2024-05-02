package com.erendogan6.sofranipaylas.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erendogan6.sofranipaylas.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore) {
    fun loginUser(email: String, password: String): MutableLiveData<User?> {
        val loggedInUser = MutableLiveData<User?>()

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseAuth.currentUser?.let { firebaseUser ->
                    val userId = firebaseUser.uid
                    val userDocRef = firestore.collection("Users").document(userId)
                    userDocRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = User(
                                about = document.getString("about") ?: "",
                                email = document.getString("email") ?: firebaseUser.email ?: "",
                                fullname = document.getString("fullname") ?: "",
                                isHost = document.getBoolean("isHost") ?: false,
                                profilePicture = document.getString("profilePicture") ?: "",
                            )
                            loggedInUser.value = user
                        } else {
                            loggedInUser.value = null
                        }
                    }
                }
            } else {
                loggedInUser.value = null
            }
        }

        return loggedInUser
    }

    fun registerUser(email: String, password: String, fullname: String): LiveData<Boolean> {
        val registrationResult = MutableLiveData<Boolean>()

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = hashMapOf("fullname" to fullname, "email" to email)
                task.result?.user?.uid?.let { userId ->
                    firestore.collection("Users").document(userId).set(user).addOnSuccessListener {
                        registrationResult.value = true
                    }.addOnFailureListener {
                        registrationResult.value = false
                    }
                }
            } else {
                registrationResult.value = false
            }
        }

        return registrationResult
    }
}
