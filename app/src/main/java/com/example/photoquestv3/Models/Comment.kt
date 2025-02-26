package com.example.photoquestv3.Models

import com.google.firebase.Timestamp

/**
 * class that holds the data for the comments
 */

data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val comment: String = "",
    val timestamp: Timestamp? = null,
    val profilePicture: String = ""
)