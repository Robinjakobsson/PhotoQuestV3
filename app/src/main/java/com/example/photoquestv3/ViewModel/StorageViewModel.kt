package com.example.photoquestv3.ViewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.StorageRepository
import kotlinx.coroutines.launch

class StorageViewModel : ViewModel() {

    private val storage = StorageRepository()

    fun uploadProfileImage(imageUri : Uri, onSuccess : (String) -> Unit, onFailure : (Exception) -> Unit) {
        viewModelScope.launch {
           try {
               val imageUrl = storage.uploadProfileImage(imageUri)
               onSuccess(imageUrl)
           } catch (e: Exception) {
               onFailure(e)
           }
        }
    }

    fun uploadPost(imageUri: Uri,description : String, isChecked : Boolean, onSuccess : () -> Unit, onFailure : (Exception) -> Unit) {
        viewModelScope.launch {
            Log.d("viewmodel","$isChecked")
            storage.uploadPost(imageUri,description,isChecked,onSuccess,onFailure)
        }
    }
}