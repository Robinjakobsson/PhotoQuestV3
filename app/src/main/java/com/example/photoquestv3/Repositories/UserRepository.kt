package com.example.photoquestv3.Repositories

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = Firebase.firestore

    /*
     */
    suspend fun deleteUser(uid: String) {
        try {
            db.collection("users").document(uid).delete().await()
            Log.d("UserRepository", "[DELETE_SUCCESS] Deleted user with uid: $uid")
        } catch (e: Exception) {
            Log.e("UserRepository", "[DELETE_ERROR] Error deleting user with uid: $uid", e)
            throw e
        }
    }


}