package com.example.photoquestv3.Repositories

import android.util.Log
import com.example.photoquestv3.Models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FireStoreRepository {

    private val db = Firebase.firestore

    suspend fun addUser(email : String, name : String, userName : String,uid : String, imageUrl : String, biography : String) {
        val user = User(email,name,userName,uid,imageUrl,biography)

        try {
            db.collection("users")
                .document(uid)
                .set(user)
                .await()

            Log.d("FireStoreRepo","User: $userName Successfully added!")
        }catch (e : Exception) {
            Log.d("FireStoreRepo","User: $userName not added...")
            throw e
        }
    }
}