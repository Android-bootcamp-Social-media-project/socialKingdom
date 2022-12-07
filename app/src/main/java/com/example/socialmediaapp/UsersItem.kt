package com.example.socialmediaapp

data class UsersItem(

    var email: String,
    val username: String,
    val password: String,
    val image: String,
    val id : Int = 0,
    val website: String? = "",
    val settings: String? = "",
    val about: String? = ""
)
