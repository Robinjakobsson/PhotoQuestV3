package com.example.photoquestv3.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FireStoreViewModel: ViewModel() {

    private val fireStoreDb = FireStoreRepository()
    private val firebaseAuth = AuthRepository()

    private val _posts = MutableLiveData<List<Post>>()
    val posts: MutableLiveData<List<Post>> = _posts //Don't delete, is needed for observing data

    //    test
    val posts123 = MutableLiveData<List<Post>>()


    private val _profileImage = MutableLiveData<String?>()
    val profileImage: MutableLiveData<String?> = _profileImage

    private val _userQuote = MutableLiveData<String?>()
    val userQuote: MutableLiveData<String?> = _userQuote

    private val _followingStatus = MutableLiveData<Boolean>()
    val followingStatus: LiveData<Boolean> = _followingStatus

    private val _followers = MutableLiveData<List<User>>()
    val followers : LiveData<List<User>> = _followers


    private val _userImages = MutableLiveData<List<String>>()
    val userImages: LiveData<List<String>> = _userImages

    private val _userPostCount = MutableLiveData<Int>()
    val userPostCount: LiveData<Int> = _userPostCount


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

    // FireStoreViewModel.kt
    fun getFollowerCount(userId: String): LiveData<Int> {
        return fireStoreDb.listenForFollowerCount(userId)
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


    fun checkFollowingStatus(currentUserId: String,targetUserId: String) : LiveData<Boolean> {
        return fireStoreDb.checkFollowingStatus(currentUserId,targetUserId)
    }

    fun unfollowUser(currentUserId: String,targetUserId: String) {
        fireStoreDb.unfollowFollower(currentUserId,targetUserId)
    }
    
    fun loadUserImages(userId: String){
        viewModelScope.launch {
            val images = fireStoreDb.fetchUserImages(userId)
            _userImages.value = images
        }
    }

    fun fetchUserPostCount(userid: String): LiveData<Int>{
        viewModelScope.launch {
            val snapshot = fireStoreDb.db.collection("posts")
                .whereEqualTo("userid",userid)
                .get()
                .await()
            _userPostCount.value = snapshot.size()
        }

        return userPostCount
    }

    fun getFollowers(uid : String, onSuccess : () -> Unit, onFailure : (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val users = fireStoreDb.getFollowers(uid,onSuccess,onFailure)
                _followers.value = users
            }catch (e : Exception) {
                Log.d("viEWModel","could not get users..")
            }
        }
    }


}