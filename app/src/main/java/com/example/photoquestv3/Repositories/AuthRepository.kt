package com.example.photoquestv3.Repositories
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
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
    private var currentUser = auth.currentUser

    fun createAccount(email: String, password: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadPicture(imageUri) // Laddar upp en bild till vår databas

                val uid = createFireBaseUser(email, password, name, imageUrl) // Funktion som returnar Uid

                saveUserToDatabase(email, username, uid, imageUrl, name, biography) // Sparar användaren till våran databas

                withContext(Dispatchers.Main) { // tillbaks till main tråden
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    fun createGoogleAccount(email: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadPicture(imageUri)
                val uid = auth.currentUser?.uid ?: throw Exception("Ingen inloggad användare")
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

    fun createFacebookAccount(email: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadPicture(imageUri)
                val uid = auth.currentUser?.uid ?: throw Exception("Ingen inloggad användare")
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

    fun signIn(email: String,password: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = signInFirebaseUser(email,password)
                currentUser = user

                if (currentUser != null) {
                    Log.d("Auth","Currentuser : ${currentUser!!.displayName}")
                }

                withContext(Dispatchers.Main) {
                    Log.d("AuthRepository","Welcome ${currentUser!!.displayName}")
                    onSuccess()
                }
            } catch (e : Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("AuthRepository","Could not sign in...$e")
                    onFailure(e)
                }
            }
        }
    }

    fun signOut(){
        auth.signOut()
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.sendPasswordResetEmail(email).await()

                withContext(Dispatchers.Main) {
                    Log.d("AuthRepository", "Password reset email sent to $email")
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("AuthRepository", "Could not send password reset email...$e")
                    onFailure(e)
                }
            }
        }
    }


    private suspend fun uploadPicture(imageUri: Uri): String {
        return storage.uploadProfileImage(imageUri)
    }

    suspend fun createFireBaseUser(email: String, password: String, name: String, imageUrl: String): String {
        val auth = Firebase.auth
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("User creation failed.")

        val profileUpdates = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(imageUrl)
        }

        user.updateProfile(profileUpdates).await()

        return user.uid
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

    private suspend fun signInFirebaseUser(email: String,password: String) : FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email,password).await()
        return authResult.user ?: throw Exception("Failed to retrieve User...")
    }

    fun getCurrentUser() : FirebaseUser? {
        return auth.currentUser
    }

    fun getUserName(): String{
        val userName = getCurrentUser()?.displayName.toString()
        return userName
    }

}
