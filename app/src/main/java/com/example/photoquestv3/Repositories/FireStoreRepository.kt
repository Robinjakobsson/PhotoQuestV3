package com.example.photoquestv3.Repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
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

//    Updated with timestamp
     suspend fun savePostToDatabase(imageUrl: String,description : String) {
         val currentUser = auth.currentUser
        val postId = UUID.randomUUID().toString()
        val post = hashMapOf(
            "postId" to postId,
            "username" to (currentUser?.displayName ?: ""),
            "profilePic" to (currentUser?.photoUrl.toString()),
            "imageUrl" to imageUrl,
            "description" to description,
            "userid" to (currentUser?.uid ?: ""),
            "likes" to 15,
            "timestamp" to FieldValue.serverTimestamp() // Timestamp implemented
        )
         try {
             db.collection("posts").document(postId).set(post).await()
                Log.d("FireStoreRepository","Successfully Created post $post!")
            }catch (e : Exception) {
                Log.d("FireStoreRepository","Failed to save post to database...", e)
        }
    }

//    Fetches all posts by time order
    suspend fun fetchPostSortedByTime() : List<Post> {

        return db.collection("posts")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get().await()
            .toObjects(Post::class.java)
    }

    fun getUsers(query: String): LiveData<List<User>> {
        val userList = MutableLiveData<List<User>>()
        val usersQuery: Query

        if (query.isEmpty()) {
            usersQuery = db.collection("users")
        } else {
            usersQuery = db.collection("users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", "$query\uf8ff")
        }

        usersQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val users = task.result?.mapNotNull { document ->
                    document.toObject(User::class.java)
                } ?: emptyList()
                userList.value = users
            }
        }
        return userList
    }
}