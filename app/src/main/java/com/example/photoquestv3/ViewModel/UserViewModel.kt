package com.example.photoquestv3.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Repositories.AuthRepository
import com.example.photoquestv3.Repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel: ViewModel()  {

    private val userRepository = UserRepository()
    private val authRepository = AuthRepository()




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


}