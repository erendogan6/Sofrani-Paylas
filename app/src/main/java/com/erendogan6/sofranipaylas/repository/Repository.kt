package com.erendogan6.sofranipaylas.repository

import android.net.Uri
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore, private val storage: FirebaseStorage) {
    suspend fun loginUser(email: String, password: String): Flow<User?> = flow {
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                val userDocRef = firestore.collection("Users").document(userId)
                val document = userDocRef.get().await()

                if (document.exists()) {
                    val user = User(
                        about = document.getString("about") ?: "",
                        email = document.getString("email") ?: firebaseUser.email ?: "",
                        fullname = document.getString("fullname") ?: "",
                        isHost = document.getBoolean("isHost") ?: false,
                        profilePicture = document.getString("profilePicture") ?: "",
                    )
                    emit(user)
                } else {
                    emit(null)
                }
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    suspend fun registerUser(email: String, password: String, fullname: String): Flow<Boolean> = flow {
        val task = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        if (task.user != null) {
            val userMap = hashMapOf("fullname" to fullname, "email" to email)
            firestore.collection("Users").document(task.user!!.uid).set(userMap).await()
            emit(true)
        } else {
            emit(false)
        }
    }


    suspend fun uploadImageAndGetUrl(imageUri: Uri): Flow<Result<String>> = flow {
        try {
            val imageRef = storage.reference.child("images/${imageUri.lastPathSegment}")
            val uploadTaskSnapshot = imageRef.putFile(imageUri).await()
            val imageUrl = uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await().toString()
            emit(Result.success(imageUrl))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    suspend fun submitPost(title: String, description: String, participants: Int, imageUrl: String, date: Timestamp): Flow<Result<Boolean>> = flow {
        try {
            val currentUserID = firebaseAuth.currentUser?.uid.orEmpty()
            val post = Post(title = title, description = description, participants = participants, images = imageUrl, date = date, status = true, userID = currentUserID)
            firestore.collection("Posts").add(post).await()
            emit(Result.success(true))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}
