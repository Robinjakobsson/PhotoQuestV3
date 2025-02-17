package com.example.photoquestv3.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.PostRepository
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _itemId = MutableLiveData<String>()
    val itemId: LiveData<String> get() = _itemId

    private val _dataChanged = MutableLiveData<Boolean>()
    val dataChanged : LiveData<Boolean> get() = _dataChanged


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

    fun deletePost(postId : String?) {

            if (postId != null) {
                val db = FirebaseFirestore.getInstance()
                val docRef = db.collection("posts").document(postId)
                docRef
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val userId = document.getString("userid")
                            Log.d("!!!", "document is fetched")
                            if (userId == currentUserId) {
                                Log.d("!!!", "currentuser is correct")
                                docRef.delete().addOnSuccessListener {
                                    _toastMessage.value = "Succesfully deleted post"
                                    _toastMessage.value = null
                                }.addOnFailureListener() {
                                    _toastMessage.value = "Failed to delete post"
                                    _toastMessage.value = null
                                }
                            } else {
                                _toastMessage.value = "You cannot deleted someone elses post!"
                                _toastMessage.value = null
                            }
                        } else {
                            Log.d("!!!", "document is null")
                        }
                    }.addOnFailureListener() { exception ->
                        Log.d("!!!", "Document not fetched")
                    }
            }
        }
}