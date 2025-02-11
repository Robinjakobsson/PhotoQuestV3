package com.example.photoquestv3.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = AuthRepository()


     fun createAccount(email: String, password: String, name: String, username: String, imageUri: Uri, biography: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            auth.createAccount(email,password,name,username,imageUri,biography,onSuccess,onFailure)
        }
    }
    fun signIn(email: String,password: String,onSuccess: () -> Unit,onFailure: (Exception) -> Unit) {
        auth.signIn(email,password,onSuccess,onFailure)

    }

    fun forgotPassword(email: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        auth.forgotPassword(email,onSuccess,onFailure)
    }

}