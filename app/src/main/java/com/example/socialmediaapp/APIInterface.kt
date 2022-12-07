package com.example.socialmediaapp

import retrofit2.*
import retrofit2.http.*

interface APIInterface {

    @GET("users/")
    fun getAllUsers(): retrofit2.Call<Users>


    @POST("users/")
    fun addUsers(@Body Users: UsersItem): retrofit2.Call<UsersItem>


    @GET("login/{username}/{password}")
    fun login(
        @Path("username") username: String,
        @Path("password") password: String
    ): Call<String>


    @GET("users/{api_key}")
    fun getUserId(@Path("api_key") id: String): Call<UsersItem>

    //---------------------------------------------------------------------

    @GET("posts/")
    fun getAllPosts(): Call<Posts>


    @POST("posts/")
    fun addPost(@Body post: PostsItem) : Call<PostsItem>


    @GET("posts/{postId}")
    fun getPost(@Path("postId") postId: Int): Call<PostsItem>


    @PUT("posts/{postId}")
    fun updatePost(@Path("postId") postId: Int, @Body postData: PostsItem): Call<PostsItem>
}