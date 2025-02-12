package com.example.photoquestv3.Repositories

import android.net.Uri
import com.example.photoquestv3.Models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostRepository {

    private val dbStorage = Firebase.storage
    private val dbFirestore = Firebase.firestore

    suspend fun createPost(
        imageUri: Uri,
        description: String,
        userId: String,
        userName: String,
        profilePic: Int
    ) : String {

        val postId = UUID.randomUUID().toString()
        val storageRef = dbStorage.reference.child("posts/$postId.jpg")
        storageRef.putFile(imageUri).await()
        val imageUrl = storageRef.downloadUrl.await().toString()

        val post = Post(
            postId = postId,
            imagePostUrl = imageUrl,
            description = description,
            userId = userId,
            username = userName,
            profilePic = profilePic,
            likes = 0
        )

        dbFirestore.collection("posts")
            .document(postId)
            .set(post)
            .await()

        return postId
    }

    suspend fun uploadPost(post: Post) {}

    suspend fun deletePost(postId: String) {}

    suspend fun updatePost(post: Post) {}

    suspend fun getAllPosts() {}

    //    Show all posts by user - ,maybe on some settings? show all the posts you have done?
    suspend fun fetchUserPosts(userId: String) :List<Post> {
        val postsByUser = dbFirestore.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return postsByUser.toObjects(Post::class.java)
    }
    //    For future references = Have the ability to see all liked posts in activity or fragment?
    suspend fun likePost(postId: String) {}



}