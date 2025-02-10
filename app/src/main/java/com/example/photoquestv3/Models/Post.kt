package com.example.photoquestv3.Models

data class Post(
    val postId: String = "",
    val username: String = "",
    val profileImage: Int = 0,
    val imagePostUrl: String = "",
    val description: String = "",
    val userId: String = "",
    val likes: Int = 0,
)