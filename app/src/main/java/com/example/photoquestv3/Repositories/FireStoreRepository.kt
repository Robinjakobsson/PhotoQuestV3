package com.example.photoquestv3.Repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FireStoreRepository {

    val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _likes = MutableLiveData<Int>()
    val likes: MutableLiveData<Int> = _likes

    private var likesListener: ListenerRegistration? = null

    /**
     * Function to add a User to the database
     */
    suspend fun addUser(
        email: String,
        name: String,
        userName: String,
        uid: String,
        imageUrl: String,
        biography: String
    ) {
        val user = User(email, name, userName, uid, imageUrl, biography)

        try {
            db.collection("users")
                .document(uid)
                .set(user)
                .await()


            Log.d("FireStoreRepository","User: $userName Successfully added!")

            val targetUserId = "Cer3dCu4h6Y0ceA0fRVILUiuJnD2"

            followUser(user.uid, targetUserId)

        }catch (e : Exception) {
            Log.d("FireStoreRepository","User: $userName not added... ${e.message}")

            throw e
        }
    }


    /**
     * Function to Save a post to the Database
     */
    suspend fun savePostToDatabase(imageUrl: String, description: String, isChecked: Boolean) {
        val currentUser = auth.currentUser
        val postId = UUID.randomUUID().toString()
        val post = hashMapOf(
            "postId" to postId,
            "username" to (currentUser?.displayName ?: ""),
            "profilePic" to (currentUser?.photoUrl.toString()),
            "imageUrl" to imageUrl,
            "description" to description,
            "userid" to (currentUser?.uid ?: ""),
            "likes" to 0,
            "likedBy" to emptyList<String>(),
            "timestamp" to FieldValue.serverTimestamp(),
            "isChecked" to isChecked// Timestamp implemented
        )
        try {
            db.collection("posts").document(postId).set(post).await()
            Log.d("FireStoreRepository", "Successfully Created post $post!")
        } catch (e: Exception) {
            Log.d("FireStoreRepository", "Failed to save post to database...", e)
        }
    }

    //    Fetches all posts by time order
    suspend fun fetchPostSortedByTime(): List<Post> {

        return db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get().await()
            .toObjects(Post::class.java)
    }

    fun getUsers(query: String): LiveData<List<User>> {
        val userList = MutableLiveData<List<User>>()
        val usersQuery: Query

        if (query.isEmpty()) {
            usersQuery = db.collection("users")
        } else {
            usersQuery = db.collection("users")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", "$query\uf8ff")
        }

        usersQuery.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val users = task.result?.mapNotNull { document ->
                    document.toObject(User::class.java)
                } ?: emptyList()
                userList.value = users
            }
        }
        return userList
    }

    suspend fun fetchUserData(uid: String): User? {
        val userRef = db.collection("users").document(uid)
            .get()
            .await()

        return userRef.toObject(User::class.java)
    }


    /**
     * Method to Follow a person
     */
    fun followUser(currentUserId: String, targetUserId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserRef = db.collection("users").document(currentUserId)
            val targetUserRef = db.collection("users").document(targetUserId)
            try {
                currentUserRef.update("following", FieldValue.arrayUnion(targetUserId)).await()
                targetUserRef.update("followers", FieldValue.arrayUnion(currentUserId)).await()
            } catch (e: Exception) {
                Log.d("FireStore", "Error following user: ${e.message}")
            }
        }
    }

    /**
     * Method to check if you are already following
     */
    fun checkFollowingStatus(currentUserId: String, targetUserId: String): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()

        val currentUserRef = db.collection("users").document(currentUserId)
        currentUserRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val following = document.get("following") as List<String>
                liveData.value = following.contains(targetUserId)
            } else {
                liveData.value = false
            }
        }

        return liveData

    }

    /**
     * Method to unfollow user
     */
    fun unfollowFollower(currentUserId: String, targetUserId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserRef = db.collection("users").document(currentUserId)
            val targetUserRef = db.collection("users").document(targetUserId)
            try {
                currentUserRef.update("following", FieldValue.arrayRemove(targetUserId)).await()
                targetUserRef.update("followers", FieldValue.arrayRemove(currentUserId)).await()
            } catch (e: Exception) {
                Log.d("FireStore", "Error during unfollow operation: ${e.message}")
            }
        }
    }

    /**
     * Method to get posts from people you follow
     */
    fun getFollowerPosts(currentUserId: String): LiveData<List<Post>> {
        val liveData = MutableLiveData<List<Post>>()
        val currentUserRef = db.collection("users").document(currentUserId)

        currentUserRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val following = documentSnapshot.get("following") as List<String>
                val userId = if (!following.contains(currentUserId)) {
                    following + currentUserId
                } else {
                    following
                }

                if (userId.isNotEmpty()) {
                    val postsRef = db.collection("posts")
                    postsRef.whereIn("userid", userId)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                Log.d("FireStore", "Error getting posts: ${error.message}")
                                return@addSnapshotListener
                            }
                            val postList = mutableListOf<Post>()
                            snapshot?.documents?.forEach { document ->
                                val isChecked = document.getBoolean("isChecked") ?: false
                                Log.d("PostFragment", "isChecked value: $isChecked")

                                val post = document.toObject(Post::class.java)
                                if (post != null) {
                                    post.isChecked = isChecked

                                    postList.add(post)
                                }
                            }

                            liveData.value = postList
                        }
                } else {
                    liveData.value = emptyList()
                }
            }
        }
        return liveData
    }

    suspend fun deletePost(postId: String, currentUserId: String?): String {
        try {
            val docRef = db.collection("posts").document(postId)
            val document = docRef.get().await()

            val userId = document.getString("userid")
            if (userId == currentUserId) {
                docRef.delete().await()
                return "Successfully deleted post"
            } else {
                return "You cannot delete someone else's post!"
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error deleting post", e)
            return "Failed to delete post"
        }
    }

    fun stopListeningToLikes() {
        likesListener?.remove()
        likesListener = null
        Log.d("PostRepository", "Successfully stopped listening to likes")
    }

    fun restartListeningToLikes(postId: String) {
        Log.d("PostRepository", "Restarting listening to likes for postId $postId")
        stopListeningToLikes()
        listenForLikes(postId)
    }

    private fun listenForLikes(postId: String) {

        if (likesListener != null) {
            return
        }

        likesListener = db.collection("posts")
            .document(postId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e("PostRepository", "Error listening to likes", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val likes = snapshot.getLong("likes")?.toInt() ?: 0
                    _likes.postValue(likes)
                } else {
                    Log.d("PostRepository", "No likes found for postId $postId")
                }
            }
    }

    suspend fun addLikesToPost(postId: String): Boolean {
        try {
            val docRef = db.collection("posts").document(postId)
            val currentUserId = auth.currentUser?.uid ?: return false

            val result = db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()
                if (likedBy.contains(currentUserId)) {
                    false
                } else {
                    transaction.update(docRef, "likes", FieldValue.increment(1))
                    transaction.update(docRef, "likedBy", FieldValue.arrayUnion(currentUserId))
                    true
                }

            }.await()
            return result
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating likes: ${e.message}", e)
            return false
        }
    }


    fun fetchFriendList(postId: String): LiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()

        db.collection("posts").document(postId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FireStoreRepository", "Error fetching likes: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val likedBy = snapshot.get("likedBy") as? List<String> ?: emptyList()

                    if (likedBy.isNotEmpty()) {
                        db.collection("users").whereIn("uid", likedBy).get()
                            .addOnSuccessListener { usersSnapshot ->
                                val friendsList = usersSnapshot.toObjects(User::class.java)
                                liveData.value = friendsList
                            }
                            .addOnFailureListener {
                                Log.e(
                                    "FireStoreRepository",
                                    "Error fetching user data: ${it.message}"
                                )
                                liveData.value = emptyList()
                            }
                    } else {
                        liveData.value = emptyList()
                    }
                } else {
                    liveData.value = emptyList()
                }
            }

        return liveData
    }


    suspend fun fetchUserImages(userId: String): List<String> {
        val snapshot = db.collection("posts")
            .whereEqualTo("userid", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
        snapshot.documents.mapNotNull { it.getString("imageUrl") }
        return snapshot.documents.mapNotNull { it.getString("imageUrl") }

    }

    // FireStoreRepository.kt
    fun listenForFollowerCount(userId: String): LiveData<Int> {
        val followerCount = MutableLiveData<Int>()
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(
                        "FireStoreRepository",
                        "Error listening to follower count: ${error.message}"
                    )
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val followers = snapshot.get("followers") as? List<String> ?: emptyList()
                    followerCount.value = followers.size
                }
            }
        return followerCount
    }
}

