package com.example.photoquestv3.Repositories

import android.util.Log
import com.example.photoquestv3.Models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FireStoreRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth


    suspend fun addUser(email : String, name : String, userName : String,uid : String, imageUrl : String, biography : String) {
        val user = User(email,name,userName,uid,imageUrl,biography)

        try {
            db.collection("users")
                .document(uid)
                .set(user)
                .await()

            Log.d("FireStoreRepo","User: $userName Successfully added!")
        }catch (e : Exception) {
            Log.d("FireStoreRepo","User: $userName not added... ${e.message}")
            throw e
        }
    }

     suspend fun savePostToDatabase(imageUrl: String,description : String) {
         val currentUser = auth.currentUser

        val postId = UUID.randomUUID().toString()
        val post = hashMapOf(
            "postId" to postId,
            "username" to (currentUser?.displayName ?: ""),
            "profilepicture" to (currentUser?.photoUrl.toString()),
            "imageUrl" to imageUrl,
            "description" to description,
            "userid" to (currentUser?.uid ?: ""),
            "likes" to 15
        )
         try {


             db.collection("posts").document(postId).set(post).await()
                Log.d("FireStoreRepository","Successfully Created post!")
            }catch (e : Exception) {
                Log.d("FireStoreRepository","Failed to save post to database...", e)
        }

    }
}