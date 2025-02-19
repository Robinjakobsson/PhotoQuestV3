package com.example.photoquestv3.Models

import com.example.photoquestv3.R
import com.google.firebase.Timestamp

data class Post(
    val postId: String = "",
    val username: String = "",
    val profilePic: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val userid: String = "",
    val likes: Int = 0,
    val timestamp: Timestamp? = null // Timestamp implemented
)
