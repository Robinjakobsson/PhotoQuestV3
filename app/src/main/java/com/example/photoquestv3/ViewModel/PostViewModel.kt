package com.example.photoquestv3.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.PostRepository
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    val firestoreVm = FireStoreViewModel()
    val fireStoreRepo = FireStoreRepository()

    private val _itemId = MutableLiveData<String>()
    val itemId: LiveData<String> get() = _itemId

    private val _listOfFriends = MutableLiveData<List<User>>()
    val listOfFriends: MutableLiveData<List<User>> get() = _listOfFriends

    private val _dataChanged = MutableLiveData<Boolean>()
    val dataChanged: LiveData<Boolean> get() = _dataChanged

    fun setItemId(id: String) {
        _itemId.value = id
    }

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> get() = _toastMessage

    private val auth = AuthRepository()
    private val postRepository = PostRepository()

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val postId = itemId.value

    fun updatePostAdapter() {
        _dataChanged.value = true
    }

    fun deletePost(postId: String?) {
        viewModelScope.launch {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (postId != null && currentUserId != null) {
                val result = fireStoreRepo.deletePost(postId, currentUserId)
                _toastMessage.value = result
                _toastMessage.value = null
                _dataChanged.value = true
                Log.d("!!!", "Deleted post")
                firestoreVm.fetchPosts()
            }
        }
    }

    fun addLikesToPost(postId: String?) {
        viewModelScope.launch {
            if (postId != null) {
                val success = fireStoreRepo.addLikesToPost(postId)
                if (success) {
                    _toastMessage.value = "Successfully added a like!"
                } else {
                    _toastMessage.value = "Failed to add a like"
                }
                _toastMessage.value = null
            }
        }
    }

    fun fetchFriendsLiked(postId: String) {
        viewModelScope.launch {
            fireStoreRepo.fetchFriendList(postId) { friendsLiked ->
                Log.d("!!!", "fetchfriendsLikes körs")
                val friends = friendsLiked.map { friendName ->
                    User(username = friendName)
                }
                _listOfFriends.value = friends
            }
        }
    }
}
