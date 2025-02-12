package com.example.photoquestv3.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Repositories.PostRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PostViewModel: ViewModel() {

    private val postRepository = PostRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts


//    Methods:

    fun createPost(
        imageUri: Uri,
        description: String,
        username: String,
        profilePic: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("No user is logged in"))
            return
        }
        val userId = currentUser.uid

        viewModelScope.launch {
            try {
                postRepository.createPost(imageUri, description, userId, username, profilePic)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }

        }
    }

    fun fetchUserPosts() {
        val currentUser = Firebase.auth.currentUser ?: return
        val userId = currentUser.uid

        viewModelScope.launch {
            try {
                val posts = postRepository.fetchUserPosts(userId)
                _posts.postValue(posts)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching user posts", e)
            }


        }
    }
}