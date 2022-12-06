package com.example.socialmediaapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPostActivity : AppCompatActivity() {

    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
    var userLogin = "anonymous"

    lateinit var etAddPostTitle : EditText
    lateinit var etAddPostText : EditText
    lateinit var btAddPost : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        //check if user login
        var tempUserLogin = intent.getStringExtra("userLogin")
        if (tempUserLogin != null){
            userLogin = tempUserLogin
        }

        etAddPostTitle = findViewById(R.id.etAddPostTitle)
        etAddPostText = findViewById(R.id.etAddPostText)
        btAddPost = findViewById(R.id.btAddPost)

        btAddPost.setOnClickListener {
            if (etAddPostTitle.text.isNotEmpty() && etAddPostText.text.isNotEmpty() ){
                if (userLogin == "anonymous"){
                    Toast.makeText(this,"You should be login", Toast.LENGTH_SHORT).show()
                }
                else {
                    addNewPost()
                }
            }
            else {
                Toast.makeText(this@AddPostActivity,"All field is required", Toast.LENGTH_SHORT).show()
            }
        }

    }


    //Add new Post
    private fun addNewPost(){
        apiInterface?.addPost(
            PostsItem(
                etAddPostTitle.text.toString(),
                etAddPostText.text.toString(),
                userLogin,
                "",
                ""
            )
        )?.enqueue(object : Callback<PostsItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<PostsItem>,
                    response: Response<PostsItem>) {

                    Toast.makeText(this@AddPostActivity,"The post has been added successfully", Toast.LENGTH_LONG).show()
                    homePageActivity()
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("MAIN", "Something went wrong!")
                }
            })
    }


    private fun homePageActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }
}