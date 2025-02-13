package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class FireStoreViewModel: ViewModel() {

    val fireStoreDb = FireStoreRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: MutableLiveData<List<Post>> = _posts

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val fetchedPosts = fireStoreDb.fetchPostSortedByTime()
                _posts.postValue(fetchedPosts)
            } catch (e: Exception) {
                Log.d("FireStoreViewModel", " [ERROR] Error fetching posts: ${e.message}")
            }
        }
    }


}