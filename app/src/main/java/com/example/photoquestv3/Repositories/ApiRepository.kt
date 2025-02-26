package com.example.photoquestv3.Repositories

import android.util.Log
import com.example.photoquestv3.API.PhotoResponse
import com.example.photoquestv3.API.UnsplashedService
import com.example.photoquestv3.Models.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

class ApiRepository {

    private val retroFit = Retrofit.Builder()
        .baseUrl("https://api.unsplash.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retroFit.create(UnsplashedService::class.java)

    suspend fun getRandomPhoto(clientId: String): PhotoResponse? {

        return try {
            val response = service.getRandomPhoto(clientId)
            if (response.isSuccessful) {

                val photo = response.body()
                Log.d("!!!", "Mottaget foto: $photo")
                return photo

            } else {
                Log.d("!!!", "\"Fel vid API-anrop: ${response.errorBody()?.string()}\"")
                null
            }
        } catch (e: Exception) {
            Log.e("ApiRepository", "Undantag fångat: ${e.localizedMessage}")
            null
        }
    }

    suspend fun saveApiPostToDatabase(imageUrl: String?, description: String?) {
        val postId = UUID.randomUUID().toString()
        val targetUserId = "Cer3dCu4h6Y0ceA0fRVILUiuJnD2"
        val db = FirebaseFirestore.getInstance()

        val user = fetchUserData(targetUserId)

        val name = "PhotoCat"
        val description1 = "Today's pic!"

        Log.d("!!!", "Hämtad användare: $user")

        val post = hashMapOf(
            "postId" to postId,
            "profilePic" to (user?.imageUrl.toString()),
            "imageUrl" to imageUrl,
            "description" to description1,
            "name" to name,
            "userid" to (targetUserId ?: ""),
            "likes" to 0,
            "likedBy" to emptyList<String>(),
            "timestamp" to FieldValue.serverTimestamp(),
            "isChecked" to false// Timestamp implemented
        )
        Log.d("!!!", "Post som ska sparas: $post")

        try {
            db.collection("posts").document(postId).set(post).await()
            Log.d("FireStoreRepository", "Successfully Created post $post!")
        } catch (e: Exception) {
            Log.d("FireStoreRepository", "Failed to save post to database...", e)
        }
    }

    suspend fun fetchUserData(uid: String): User? {

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)
            .get()
            .await()

        Log.d("!!!", "User data from Firestore: ${userRef.data}")
        return userRef.toObject(User::class.java)
    }

}