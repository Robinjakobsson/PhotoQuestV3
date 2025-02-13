package com.example.photoquestv3.ViewModel

import android.net.Uri
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.Repositories.StorageRepository
import kotlinx.coroutines.launch

class StorageViewModel : ViewModel() {

    private val storage = StorageRepository()

    fun uploadPost(imageUri: Uri,description : String) {
        viewModelScope.launch {
            storage.uploadPost(imageUri,description)
        }
    }
}