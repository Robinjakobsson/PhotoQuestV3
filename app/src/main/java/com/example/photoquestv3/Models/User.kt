package com.example.photoquestv3.Models

import java.io.Serializable

data class User(
    val email : String = "",
    val name : String = "",
    val username : String = "",
    val uid : String = "",
    val imageUrl : String = "",
    val biography : String = "",
    val usernamesearch : String = "",
    val followers : List<String> = listOf(),
    val following : List<String> = listOf()) : Serializable {
}