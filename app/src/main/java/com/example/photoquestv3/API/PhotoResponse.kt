package com.example.photoquestv3.API

import retrofit2.http.Url

data class PhotoResponse(
    val id: String,
    var description : String?,
    val urls : Urls
)

data class Urls (
    val regular : String
)
