package com.example.photoquestv3.Models

import com.google.firebase.Timestamp

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val comment: String = "",
    val timestamp: Timestamp? = null
)