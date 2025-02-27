package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.CommentRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.PostRepository
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    val firestoreVm = FireStoreViewModel()
    val fireStoreRepo = FireStoreRepository()

    private val postRepository = PostRepository()


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


    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val postId = itemId.value

    val likes: LiveData<Int> get() = fireStoreRepo.likes

    fun updatePostAdapter() {
        _dataChanged.value = true
    }

    fun updatePostText(postId: String, newText: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        postRepository.updatePostText(postId, newText, onSuccess, onFailure)
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
                setItemId(postId)

                val success = fireStoreRepo.addLikesToPost(postId)
                if (success) {
                    _toastMessage.value = "Successfully added a like!"
                } else {
                    _toastMessage.value = "Already liked"
                }
                _dataChanged.value = true
                _toastMessage.value = null
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        fireStoreRepo.stopListeningToLikes()
    }

    fun fetchFriendList(postId: String) : LiveData<List<User>> {
       return fireStoreRepo.fetchFriendList(postId)
    }

}


    
    

