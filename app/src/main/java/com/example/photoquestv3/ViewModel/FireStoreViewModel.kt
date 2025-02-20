package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class FireStoreViewModel: ViewModel() {

    private val fireStoreDb = FireStoreRepository()
    private val firebaseAuth = AuthRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: MutableLiveData<List<Post>> = _posts //Don't delete, is needed for observing data

    //    test
    val posts123 = MutableLiveData<List<Post>>()

    private val _userImages = MutableLiveData<List<String>>()
    val userImages: LiveData<List<String>> = _userImages

    //    Call in Fragment or Activity.
    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val fetchedPosts = fireStoreDb.fetchPostSortedByTime()
                _posts.postValue(fetchedPosts)
                Log.d("!!!", "Fetching post correct")

            } catch (e: Exception) {
                Log.d("FireStoreViewModel", " [ERROR] Error fetching posts: ${e.message}")
            }
        }
    }

    fun getUsers(query: String): LiveData<List<User>> {
        return fireStoreDb.getUsers(query)
    }

    suspend fun fetchUserData(uid : String) : User? {
        return fireStoreDb.fetchUserData(uid)
    }

    fun followUser(currentUserId : String, targetUserId : String) {
        fireStoreDb.followUser(currentUserId,targetUserId)
    }

    fun getPostsFromFollowing(currentUserId: String) : LiveData<List<Post>> {
        return fireStoreDb.getFollowerPosts(currentUserId)
    }


    fun fetchPosts123() {
        fireStoreDb.db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FireStoreViewModel", "Error listening to posts: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    posts123.postValue(snapshot.toObjects(Post::class.java))
                }
            }
    }

    fun loadUserImages(){
        viewModelScope.launch {
            val images = fireStoreDb.fetchUserImages()
            _userImages.value = images
        }
    }

}