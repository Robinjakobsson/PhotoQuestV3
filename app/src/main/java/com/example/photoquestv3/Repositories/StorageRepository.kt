package com.example.photoquestv3.Repositories

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class StorageRepository {

    private val storage = Firebase.storage

    suspend fun uploadProfileImage(imageUri : Uri) : String {
        val profileImageRef = storage.reference.child("profile_images/${UUID.randomUUID()}.jpg")
        profileImageRef.putFile(imageUri).await()
        return  profileImageRef.downloadUrl.await().toString()
    }

}