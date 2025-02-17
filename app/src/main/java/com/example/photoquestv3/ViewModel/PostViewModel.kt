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

    //Get id on post
    private val _itemId = MutableLiveData<String>()
    val itemId: LiveData<String> get() = _itemId

    fun setItemId(id: String) {
        _itemId.value = id
    }





}