package com.example.socialmediaapp

data class UsersItem(

    var email: String,
    val username: String,
    val password: String,
    val id : Int = 0,
    val image: String? = "",
    val website: String? = "",
    val settings: String? = "",
    val about: String? = ""
)
