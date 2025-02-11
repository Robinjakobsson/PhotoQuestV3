package com.example.photoquestv3.Repositories

import com.example.photoquestv3.Models.Post
const val COLLECTION_POSTS = "posts"
const val TAG = "PostRepository"

class PostRepository {


   fun createPost(
      post: Post,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
   ) {

   }

}