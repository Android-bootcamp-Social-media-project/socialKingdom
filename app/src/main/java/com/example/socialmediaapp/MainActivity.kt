package com.example.socialmediaapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
    var userLogin = "anonymous"
    var postsData = arrayListOf<PostsItem>()
    private lateinit var myRV : RecyclerView
    var adapter = PostsAdapter( this, postsData)

    lateinit var btnlogInSignUp : Button
    lateinit var btAddPost : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check if user login
        var tempUserLogin = intent.getStringExtra("userLogin")
        if (tempUserLogin != null){
            userLogin = tempUserLogin
        }

        myRV = findViewById(R.id.rvPosts)
        getAllPosts()
        myRV.adapter = adapter
        myRV.layoutManager = LinearLayoutManager(this)

        btnlogInSignUp = findViewById(R.id.btnlogInSignUp)
        btnlogInSignUp.setOnClickListener {
            if (userLogin == "anonymous"){
                signUp()
            }
            else {
                Toast.makeText(this,"$userLogin  You're already logged in",
                    Toast.LENGTH_SHORT).show()
            }
        }

        btAddPost = findViewById(R.id.btAddPost)
        btAddPost.setOnClickListener {
            if (userLogin == "anonymous"){
                Toast.makeText(this,"You should be login", Toast.LENGTH_SHORT).show()
            }
            else {
                addNewPost()
            }
        }
    }


    fun signUp() {
        val intent = Intent(this, LoginPageActivity::class.java)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }


    fun showPost(postId: Int) {
        val intent = Intent(this, ShowPostActivity::class.java)
        intent.putExtra("postId",postId)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }


    fun addNewPost() {
        val intent = Intent(this, AddPostActivity::class.java)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }


    //Request all posts from API
    private fun getAllPosts(){
        apiInterface?.getAllPosts()
            ?.enqueue(object : Callback<Posts> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<Posts>,
                    response: Response<Posts>) {

                    if (response.body() != null) {
                        var body = response.body()
                        if (body != null)
                        {
                            postsData.addAll(body)
                            adapter.update(postsData)
                        }
                    }
                }

                override fun onFailure(call: Call<Posts>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }

}