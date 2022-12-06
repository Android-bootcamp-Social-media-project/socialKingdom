package com.example.socialmediaapp

data class PostsItem (

    val title: String,
    val text: String,
    val user: String,
    var comments: String,
    var likes: String,
    val id: Int = 0
)