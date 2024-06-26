package com.erendogan6.sofranipaylas.repository

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import com.erendogan6.sofranipaylas.extensions.getAddress
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firestore: FirebaseFirestore, private val storage: FirebaseStorage, private val context: Context) {

    suspend fun changePassword(currentPassword: String, newPassword: String): Flow<Boolean> = flow {
        val user = firebaseAuth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            try {
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }
        } else {
            emit(false)
        }
    }

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

    suspend fun uploadImageAndGetUrl(imageUri: Uri, path: String): Flow<Result<String>> = flow {
        try {
            val imageRef = storage.reference.child("$path/${imageUri.lastPathSegment}")
            val uploadTaskSnapshot = imageRef.putFile(imageUri).await()
            val imageUrl = uploadTaskSnapshot.metadata?.reference?.downloadUrl?.await().toString()
            emit(Result.success(imageUrl))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }


    suspend fun updateProfilePicture(imageUri: Uri): Flow<Result<String>> = flow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            try {
                uploadImageAndGetUrl(imageUri, "profile/${user.uid}").collect { result ->
                    if (result.isSuccess) {
                        val imageUrl = result.getOrNull()
                        if (imageUrl != null) {
                            val userDocRef = firestore.collection("Users").document(user.uid)
                            userDocRef.update("profilePicture", imageUrl).await()
                            emit(Result.success(imageUrl))
                        } else {
                            emit(Result.failure(Exception("Image URL is null")))
                        }
                    } else {
                        emit(Result.failure(result.exceptionOrNull() ?: Exception("Unknown error")))
                    }
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        } else {
            emit(Result.failure(Exception("User not authenticated")))
        }
    }

    suspend fun submitPost(title: String, description: String, participants: Int, imageUrl: String, date: Timestamp, latitude: Double, longitude: Double): Flow<Result<Boolean>> = flow {
        try {
            val currentUserID = firebaseAuth.currentUser?.uid.orEmpty()
            Log.d("submitPost", "Current User ID: $currentUserID")

            if (currentUserID.isNotEmpty()) {
                val post =
                    Post(title = title, description = description, maxParticipants = participants, image = imageUrl, date = date, eventStatus = true, hostID = currentUserID, latitude = latitude, longitude = longitude, createDate = Timestamp.now())
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
        try {
            val postsSnapshot = firestore.collection("Posts").get().await()
            val posts = postsSnapshot.map { document ->
                val post = document.toObject(Post::class.java)
                post.postID = document.id

                val userSnapshot = firestore.collection("Users").document(post.hostID).get().await()
                post.hostUserName = userSnapshot.getString("userName") ?: ""

                post
            }

            emit(posts)
        } catch (e: Exception) {
            Log.e("Repository", "Error getting posts", e)
            emit(emptyList())
        }
    }


    fun getCurrentUser(): Flow<User?> = flow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userDocRef = firestore.collection("Users").document(user.uid)
            val document = userDocRef.get().await()
            if (document.exists()) {
                val currentUser = User(about = document.getString("about") ?: "", email = document.getString("email") ?: user.email ?: "", isHost = document.getBoolean("isHost")
                    ?: false, name = document.getString("name") ?: "", phone = document.getString("phone"), profilePicture = document.getString("profilePicture")
                    ?: "", role = document.getString("role") ?: "", surname = document.getString("surname") ?: "", userName = document.getString("userName") ?: "")
                emit(currentUser)
            } else {
                emit(null)
            }
        } else {
            emit(null)
        }
    }

    suspend fun fetchAddress(latLng: LatLng): String {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            var result: String? = null
            geocoder.getAddress(latLng.latitude, latLng.longitude) { address ->
                result = address?.getAddressLine(0)
            }
            result ?: "Adres bulunamadı"
        }
    }

    suspend fun getPostById(postId: String): Post? {
        return try {
            val document = firestore.collection("Posts").document(postId).get().await()
            document.toObject(Post::class.java)
        } catch (e: Exception) {
            Log.e("Repository", "Error getting post by id: $postId", e)
            null
        }
    }

    suspend fun joinPost(postId: String): Result<Boolean> {
        val currentUser = firebaseAuth.currentUser
        return if (currentUser != null) {
            val currentUserID = currentUser.uid
            Log.d("Repository", "Current User ID: $currentUserID")

            try {
                val postSnapshot = firestore.collection("Posts").document(postId).get().await()
                val post = postSnapshot.toObject(Post::class.java)

                if (post?.participants?.contains(currentUserID) == true) {
                    return Result.failure(Exception("User already joined"))
                }

                firestore.collection("Posts").document(postId).update("participants", FieldValue.arrayUnion(currentUserID)).await()
                Result.success(true)
            } catch (e: Exception) {
                Log.e("Repository", "Error joining post", e)
                Result.failure(e)
            }
        } else {
            Log.e("Repository", "User not authenticated")
            Result.failure(Exception("User not authenticated"))
        }
    }

    suspend fun getNearbyPosts(latitude: Double, longitude: Double): List<Post> {
        return try {
            val postsSnapshot = firestore.collection("Posts").whereEqualTo("eventStatus", true).get().await()

            postsSnapshot.documents.mapNotNull { document ->
                val post = document.toObject(Post::class.java)
                if (post != null && isNearby(post, latitude, longitude)) {
                    post.postID = document.id
                    post
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isNearby(post: Post, latitude: Double, longitude: Double): Boolean {
        val postLocation = GeoPoint(post.latitude, post.longitude)
        val userLocation = GeoPoint(latitude, longitude)
        val distance = FloatArray(1)
        android.location.Location.distanceBetween(userLocation.latitude, userLocation.longitude, postLocation.latitude, postLocation.longitude, distance)
        return distance[0] <= 50000
    }

}
