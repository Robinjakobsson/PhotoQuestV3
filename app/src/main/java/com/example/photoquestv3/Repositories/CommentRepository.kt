package com.example.photoquestv3.Repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.Comment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp

const val TAG = "CommentRepository"

class CommentRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: MutableLiveData<List<Comment>> = _comments

    private var commentsListener: ListenerRegistration? = null

    fun addComment(
        postId: String,
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
            comment = commentText,
            timestamp = Timestamp.now()
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

    fun startListeningToComments(postId: String) {

        if (commentsListener != null) {
            Log.d(TAG, "[LISTEN] Already listening to comments for postId $postId")
            return

        }

        commentsListener = db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "[LISTEN] Error listening to comments", error)
                    return@addSnapshotListener
                }

                val commentsList = mutableListOf<Comment>()

                if (snapshot != null) {
                    Log.d(TAG, "[LISTEN] Received snapshot with ${snapshot.size()} documents")
                    for (document in snapshot.documents) {

                        val comment = document.toObject(Comment::class.java)
                        if (comment != null) {
                            commentsList.add(comment)
                        }
                    }
                }
                Log.d(TAG, "[LISTEN] Posting ${commentsList.size} comments to LiveData")
                _comments.postValue(commentsList)
            }
    }

    fun stopListeningToComments() {
        commentsListener?.remove()
        commentsListener = null
        Log.d(TAG, "[LISTEN] Successfully stopped listening to comments")
    }

    fun restartListeningToComments(postId: String) {
        Log.d(TAG, "[LISTEN] Restarting listening to comments for postId $postId")
        stopListeningToComments()
        startListeningToComments(postId)
    }

}