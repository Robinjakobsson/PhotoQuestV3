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
import com.google.firebase.firestore.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    val firestoreVm = FireStoreViewModel()

    private val _itemId = MutableLiveData<String>()
    val itemId: LiveData<String> get() = _itemId

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

        if (postId != null) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("posts").document(postId)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("!!!", "Listener failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d("!!!", "Current data: ${snapshot.data}")

                    _dataChanged.value = true
                    firestoreVm.fetchPosts()


                } else {
                    Log.d("!!!", "Current data: null")
                }
            }

            docRef
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userId = document.getString("userid")
                        Log.d("!!!", "document is fetched")
                        if (userId == currentUserId) {
                            Log.d("!!!", "currentuser is correct")
                            docRef.delete().addOnSuccessListener {
                                _toastMessage.value = "Successfully deleted post"
                                _toastMessage.value = null

                                _dataChanged.value = true
                                firestoreVm.fetchPosts()

                            }.addOnFailureListener() {
                                _toastMessage.value = "Failed to delete post"
                                _toastMessage.value = null
                            }
                        } else {
                            _toastMessage.value = "You cannot delete someone else's post!"
                            _toastMessage.value = null
                        }
                    } else {
                        Log.d("!!!", "Document is null")
                    }
                }.addOnFailureListener() { exception ->
                    Log.d("!!!", "Document not fetched")
                }

        }
    }

    fun addLikesToPost(postId: String?) {

        if (postId != null) {
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("posts").document(postId)

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("!!!", "Listener failed", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d("!!!", "Current data: ${snapshot.data}")

                } else {
                    Log.d("!!!", "Current data: null")
                }

            }

            docRef
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        var likeCounter = document.getField<Int>("likes")

                        var newLikeCounter = likeCounter.toString().toInt()

                        newLikeCounter++
                        docRef.update("likes", newLikeCounter).addOnSuccessListener {

                            Log.d("!!!", "Updated likes ++")

                        }.addOnFailureListener() {
                            Log.d("!!!", "Failed to update likes")
                        }
                    }
                }
        }
    }
}