package com.example.photoquestv3.Repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FireStoreRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

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

            Log.d("FireStoreRepo", "User: $userName Successfully added!")
        } catch (e: Exception) {
            Log.d("FireStoreRepo", "User: $userName not added... ${e.message}")
            throw e
        }
    }

    //    Updated with timestamp
    suspend fun savePostToDatabase(imageUrl: String, description: String) {
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
            "timestamp" to FieldValue.serverTimestamp() // Timestamp implemented
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


    fun dbCollectionUser(): CollectionReference {
        return db.collection("users")
    }


    suspend fun fetchProfileImage(): String? {
        val currentUser = auth.currentUser ?: return null
        return try {
            val documentSnapshot = db.collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            documentSnapshot.getString("imageUrl")
        } catch (e: Exception) {
            Log.e("FireStoreRepository", "Error fetching profile image: ${e.message}", e)
            null
        }
    }

    suspend fun fetchUserData(uid : String) : User? {
        val userRef = db.collection("users").document(uid)
            .get()
            .await()

        return userRef.toObject(User::class.java)
    }



    suspend fun fetchUserQuote(): String? {
        val currentUser = auth.currentUser ?: return null
        return try {
            auth.currentUser
            val documentSnapshot = db.collection("users")
                .document(currentUser.uid)
                .get()
                .await()
            documentSnapshot.getString("biography")

        } catch (e: Exception) {
            Log.d("FireStoreRepository", "error")

            null
        }
    }
    fun followUser(currentUserId : String, targetUserId : String) {
        val currentUserRef = db.collection("users").document(currentUserId)
        val targetUserRef = db.collection("users").document(targetUserId)

        currentUserRef.update("following", FieldValue.arrayUnion(targetUserId))

        targetUserRef.update("followers", FieldValue.arrayUnion(currentUserId))

    }
    fun getFollowerPosts(currentUserId: String) : LiveData<List<Post>> {
        val liveData =  MutableLiveData<List<Post>>()

        val currentUserRef = db.collection("users").document(currentUserId)

        currentUserRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                val following = documentSnapshot.get("following") as List<String>

                val postsRef = db.collection("posts")
                postsRef.whereIn("userid", following)
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .addSnapshotListener {snapshot, exception ->
                        if (exception != null) {
                            Log.d("FireStore","Error getting posts")
                            return@addSnapshotListener
                        }

                        val postslist = mutableListOf<Post>()
                        snapshot?.documents?.forEach {documents ->
                            val post = documents.toObject(Post::class.java)
                            if (post != null) {
                                postslist.add(post)
                            }
                        }

                        liveData.value = postslist
                    }
            }
        }
        return liveData
    }


    suspend fun fetchUserImages(): List<String> {
        val currentUser = auth.currentUser ?: return emptyList()
        val snapshot = db.collection("posts")
            .whereEqualTo("userid", currentUser.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
        snapshot.documents.mapNotNull { it.getString("imageUrl") }
        return snapshot.documents.mapNotNull { it.getString("imageUrl") }

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

    suspend fun addLikesToPost(postId: String): Boolean {

        val currentUser = FirebaseAuth.getInstance().currentUser?.uid

        try {
            val docRef = db.collection("posts").document(postId)
            val document = docRef.get().await()

            if (document.exists()) {

                docRef.update("likedBy", FieldValue.arrayUnion(currentUser)).await()
                val likeCounter = document.getLong("likes") ?: 0

                val friendsLiked = document.get("likedBy") as? List<String>

                if (friendsLiked?.contains(currentUser) == false) {
                    val newLikeCounter = likeCounter + 1
                    docRef.update("likes", newLikeCounter).await()
                    Log.d("!!!", "Likes +")
                    return true

                } else {

                    val newLikeCounter = likeCounter - 1
                    docRef.update("likes", newLikeCounter).await()
                    docRef.update("likedBy", FieldValue.arrayRemove(currentUser)).await()
                    Log.d("!!!", "Likes -")
                    return false
                }

            } else {
                Log.d("!!!", "Post doesnt exist.")
                return false
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating likes", e)
            return false
        }
    }


    suspend fun fetchFriendList(postId: String, callback: (List<String>) -> Unit) {

        Log.d("!!!", "fetchFriendsList from repo körs")
        try {
            val docRef = db.collection("posts").document(postId)
            val document = docRef.get().await()

            if (document.exists()) {
                val friendsLiked = document.get("likedBy") as? List<String>

                if (friendsLiked != null) {
                    callback(friendsLiked)

                    Log.d("!!!", "Friends fetched. ${friendsLiked}")
                }
            }
        } catch (e: Exception) {
            Log.e("!!!", "Error fetching friends", e)
        }
    }
}


}

