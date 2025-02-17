package com.example.photoquestv3.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val comment: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null
)