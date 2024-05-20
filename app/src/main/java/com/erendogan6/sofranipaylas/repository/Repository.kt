package com.erendogan6.sofranipaylas.repository

import android.net.Uri
import android.util.Log
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
                    val user = User(about = document.getString("about") ?: "", email = document.getString("email") ?: firebaseUser.email ?: "", isHost = document.getBoolean("isHost")
                        ?: false, name = document.getString("name") ?: "", phone = document.getString("phone"), profilePicture = document.getString("profilePicture")
                        ?: "", role = document.getString("role") ?: "", surname = document.getString("surname") ?: "", userName = document.getString("userName") ?: "")
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


    suspend fun registerUser(email: String, password: String, name: String, surname: String, phone: String, userName: String, isHost: Boolean): Flow<Boolean> = flow {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                val user = User(about = "", email = email, name = name, surname = surname, phone = phone, profilePicture = "", role = "user", userName = userName, isHost = isHost)
                firestore.collection("Users").document(userId).set(user).await()
                emit(true)
            } else {
                emit(false)
            }
        } catch (e: Exception) {
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
            Log.d("submitPost", "Current User ID: $currentUserID")

            if (currentUserID.isNotEmpty()) {
                val post = Post(title = title, description = description, maxParticipants = participants, image = imageUrl, date = date, eventStatus = true, hostID = currentUserID)
                Log.d("submitPost", "Post Data: $post")

                firestore.collection("Posts").add(post).await()
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception("User not authenticated")))
            }
        } catch (e: Exception) {
            Log.e("submitPost", "Error submitting post", e)
            emit(Result.failure(e))
        }
    }

    fun getPosts(): Flow<List<Post>> = flow {
        val postsSnapshot = firestore.collection("Posts").get().await()
        val posts = postsSnapshot.toObjects(Post::class.java)

        for (post in posts) {
            val userSnapshot = firestore.collection("Users").document(post.hostID).get().await()
            val userName = userSnapshot.getString("userName") ?: ""
            post.hostUserName = userName
        }

        emit(posts)
    }

}
