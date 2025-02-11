package com.example.photoquestv3.Repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository {

    private val auth = Firebase.auth
    private val storage = StorageRepository()
    private val db = FireStoreRepository()

    fun createAccount(email: String, password: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadPicture(imageUri)

                val uid = createFireBaseUser(email, password)

                saveUserToDatabase(email, username, uid, imageUrl, name, biography)

                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    private suspend fun uploadPicture(imageUri: Uri): String {
        return storage.uploadProfileImage(imageUri)
    }

    private suspend fun createFireBaseUser(email: String, password: String): String {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        return authResult.user?.uid ?: throw Exception("User creation failed.")
    }

    private suspend fun saveUserToDatabase(email: String, username: String, uid: String, imageUrl: String, name: String, biography: String) {
        Log.d("AuthRepository", " Saving user to Firestore: UID=$uid, Name=$name, Username=$username, Email=$email")
        try {
            db.addUser(email, name, username, uid, imageUrl, biography)
            Log.d("AuthRepository", "User successfully saved in Firestore!")
        } catch (e: Exception) {
            Log.e("AuthRepository", " Error saving user in Firestore: ${e.message}", e)
            throw e
        }
    }
}
