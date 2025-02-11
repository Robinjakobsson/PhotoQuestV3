package com.example.photoquestv3.Repositories

import com.example.photoquestv3.Models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PostRepository {

    private val db = Firebase.firestore
    private val dbStorage = Firebase.storage

    fun addPost(post: Post) {}

    fun uploadPost(post: Post) {}

    fun deletePost(postId: String) {}

    fun updatePost(post: Post) {}

    fun getAllPosts() {}

//    Show all posts by user
    fun getPostsByUserId(userId: String) {}

//    For future references = Have the ability to see all liked posts in activity or fragment?
    fun likePost(postId: String) {}



}