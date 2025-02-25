package com.example.photoquestv3.Repositories

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostRepository1 {

    private val db = Firebase.firestore

    fun updatePostText(
        postId: String,
        newText: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        Log.d("PostRepository", "[UPDATE] Updating comments for post $postId")
        db.collection("posts").document(postId)
            .update("description", newText)
            .addOnFailureListener {
                Log.d(TAG, "[UPDATE] Post text successfully updated")
                onSuccess()
            }.addOnFailureListener {
                Log.e(TAG, "[UPDATE] Error updating post text")
                onFailure(Exception("Error updating post text"))
            }
    }
}
