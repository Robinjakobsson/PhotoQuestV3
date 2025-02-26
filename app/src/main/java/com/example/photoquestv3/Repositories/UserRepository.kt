package com.example.photoquestv3.Repositories

import android.util.Log
import com.example.photoquestv3.Models.User
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = Firebase.firestore

    suspend fun updateUser(user: User) {
        try {
            db.collection("users").document(user.uid)
                .set(user, SetOptions.merge())
                .await()
            Log.d("UserRepository", "[UPDATE_SUCCESS] Updated user with uid: ${user.uid}")

        } catch (e: Exception) {
            Log.e("UserRepository", "[UPDATE_ERROR] Error updating user", e)
            throw e
        }
    }

    suspend fun updateUserField(uid: String, field: String, value: Any) {
        try {
            db.collection("users").document(uid)
                .update(field, value)
                .await()
            Log.d("UserRepository", "[UPDATE_SUCCESS] Updated field $field for user with uid: $uid")
        } catch (e: Exception) {
            Log.e("UserRepository", "[UPDATE_ERROR] Error updating field $field for user with uid: $uid", e)
            throw e
        }
    }


    /**
     * Deletes a user from the database.
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