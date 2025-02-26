package com.example.photoquestv3.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoquestv3.API.PhotoResponse
import com.example.photoquestv3.Repositories.ApiRepository
import kotlinx.coroutines.launch

class ApiViewModel : ViewModel(){

    private val repository: ApiRepository = ApiRepository()

    fun fetchRandomPhoto ( clientId : String, onResult: (PhotoResponse?) -> Unit) {
        viewModelScope.launch {
            val photo = repository.getRandomPhoto(clientId)




            onResult(photo)


        }
    }
}