package com.example.photoquestv3.Repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {

    private val storage = Firebase.storage
    private val db = FireStoreRepository()

    suspend fun uploadProfileImage(imageUri : Uri) : String {
        val profileImageRef = storage.reference.child("profile_images/${UUID.randomUUID()}.jpg")
        profileImageRef.putFile(imageUri).await()
        return  profileImageRef.downloadUrl.await().toString()
    }
    suspend fun uploadPost(imageUri: Uri, description : String, isChecked : Boolean, onSuccess : () -> Unit, onFailure : (Exception) -> Unit ) {
        try {
            val imageFileName = "posts/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(imageFileName)

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            savePostToDataBase(downloadUrl,description,isChecked)
            onSuccess()
            Log.d("storage","$isChecked")
        } catch (e : Exception) {
            onFailure(e)
            Log.d("Could not upload picture to Database...", e.toString())
        }
    }

    suspend fun savePostToDataBase(imageUrl: String, description: String, isChecked: Boolean) {
        db.savePostToDatabase(imageUrl,description,isChecked)
    }

}