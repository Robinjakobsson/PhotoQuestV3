package com.example.photoquestv3.Repositories

import com.example.photoquestv3.Models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val COLLECTION_POSTS = "posts"
const val TAG = "PostRepository"

class PostRepository {

   private val db = Firebase.firestore

   fun createPost(
      post: Post,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
   ) {

      db.collection(COLLECTION_POSTS).document(post.postId)


   }

}