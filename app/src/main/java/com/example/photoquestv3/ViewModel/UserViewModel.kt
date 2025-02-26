package com.example.photoquestv3.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel: ViewModel()  {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()

    val userData: LiveData<User?> = userRepository.userData

    init {
        val currentUser = authRepository.getCurrentUserUid()
        userRepository.restartListening(currentUser)
    }

    fun updateUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun updateUserField(uid: String, field: String, value: Any, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository.updateUserField(uid, field, value)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }


    fun deleteUserAccount(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: throw Exception("No logged in user")
                userRepository.deleteUser(currentUser.uid)
                authRepository.deleteAuthUser()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        userRepository.stopListening()
    }


}