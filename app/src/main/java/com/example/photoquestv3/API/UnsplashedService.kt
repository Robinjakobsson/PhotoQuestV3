package com.example.photoquestv3.API


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashedService {

    @GET("/photos/random")

    suspend fun getRandomPhoto(@Query("client_id") clientID : String) : Response<PhotoResponse>

}