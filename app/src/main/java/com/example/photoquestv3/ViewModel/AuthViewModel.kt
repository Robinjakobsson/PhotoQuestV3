package com.example.photoquestv3.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.StorageRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = AuthRepository()
    private val _userName = MutableLiveData<String>()
    val username: MutableLiveData<String> =_userName


    fun createGoogleAccount(email: String,password: String,name: String,username: String,imageUri: Uri,biography: String,onSuccess: () -> Unit, onFailure: (Exception) -> Unit){
        viewModelScope.launch {
            auth.createGoogleAccount(email, name, username, imageUri, biography, onSuccess, onFailure)
        }
    }

     fun createAccount(email: String, password: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            auth.createAccount(email,password,name,username,imageUri,biography,onSuccess,onFailure)
        }
    }
    fun signIn(email: String,password: String,onSuccess: () -> Unit,onFailure: (Exception) -> Unit) {
        auth.signIn(email,password,onSuccess,onFailure)

    }

    fun signOut(){
        auth.signOut()
    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.forgotPassword(email,onSuccess,onFailure)
    }

    fun getCurrentUser() : FirebaseUser? {
        return auth.getCurrentUser()
    }

}