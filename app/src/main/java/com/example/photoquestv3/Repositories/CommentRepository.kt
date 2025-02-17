package com.example.photoquestv3.Repositories

import android.util.Log
import com.example.photoquestv3.Models.Comment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val TAG = "CommentRepository"

class CommentRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun addComment(postId: String,
                   commentText: String,
                   onSuccess: () -> Unit,
                   onFailure: (Exception) -> Unit
    ) {

        val user = auth.currentUser
        if (user == null) {
            Log.e(TAG, "[CREATE] User not authenticated")
            onFailure(Exception("User not authenticated"))
            return
        }

        val comment = Comment(
            postId = postId,
            userId = user.uid,
            username = user.displayName ?: "No user here",
            comment = commentText
        )

        Log.d(TAG, "[CREATE] Creating comment for post $postId, by user ${user.uid}")
        db.collection("comments")
            .add(comment)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "[CREATE] Comment successfully added with ID: ${docRef.id}")
                onSuccess()
            }.addOnFailureListener {
                Log.e(TAG, "[CREATE] Error adding comment", it)
                onFailure(it)
            }
    }

}