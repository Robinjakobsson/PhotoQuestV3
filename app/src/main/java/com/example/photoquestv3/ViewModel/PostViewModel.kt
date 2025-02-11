package com.example.photoquestv3.ViewModel

import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Repositories.PostRepository

class PostViewModel {

    private val postRepository = PostRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: MutableLiveData<List<Post>> = _posts

//    Methods:



}