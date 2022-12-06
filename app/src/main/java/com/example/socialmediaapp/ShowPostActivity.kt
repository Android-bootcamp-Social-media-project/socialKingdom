package com.example.socialmediaapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class ShowPostActivity : AppCompatActivity() {

    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
    var postId = 0
    var userLogin = "anonymous"

    lateinit var tvViewPostTitle : TextView
    lateinit var tvViewPostText : TextView
    lateinit var tvViewPostLikes : TextView
    lateinit var tvViewPostComments : TextView

    lateinit var etViewPostComment : EditText
    lateinit var btLeaveComment : Button
    lateinit var btLike : Button

    lateinit var oldPostData : PostsItem

    var likesList = arrayListOf<String>()
    var commentsList = arrayListOf<String>()
    private lateinit var myRV : RecyclerView
    var adapter = CommentsAdapter(commentsList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)

        //check if user login
        var tempUserLogin = intent.getStringExtra("userLogin")
        if (tempUserLogin != null){
            userLogin = tempUserLogin
        }

        postId = intent.getIntExtra("postId", 0)

        myRV = findViewById(R.id.rvViewPostComments)
        myRV.adapter = adapter
        myRV.layoutManager = LinearLayoutManager(this)

        tvViewPostTitle = findViewById(R.id.tvViewPostTitle)
        tvViewPostText = findViewById(R.id.tvViewPostText)
        tvViewPostLikes = findViewById(R.id.tvViewPostLikes)
        tvViewPostComments = findViewById(R.id.tvViewPostComments)

        etViewPostComment = findViewById(R.id.etViewPostComment)
        btLeaveComment = findViewById(R.id.btLeaveComment)
        btLike = findViewById(R.id.btLike)

        getPost()
        btLeaveComment.setOnClickListener {
            if (etViewPostComment.text.isNotEmpty()){
                if (userLogin == "anonymous"){
                    Toast.makeText(this,"You should be login", Toast.LENGTH_SHORT).show()
                }
                else {
                    updatePostComments()
                    etViewPostComment.clearFocus()
                }
            }
            else {
                Toast.makeText(this@ShowPostActivity,
                    "All field is required", Toast.LENGTH_SHORT).show()
            }
        }

        btLike.setOnClickListener {
            if (userLogin == "anonymous"){
                Toast.makeText(this,"You should be login", Toast.LENGTH_SHORT).show()
            }
            else {
                updatePostLikes()
            }
        }
    }


    //Request post data from API
    private fun getPost(){
        apiInterface?.getPost(postId)
            ?.enqueue(object : Callback<PostsItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<PostsItem>,
                    response: Response<PostsItem>) {

                    if (response.body() != null) {
                        var body = response.body()
                        if (body != null)
                        {
                            //Comments data and count
                            if (body.comments.isNotEmpty()){
                                val comment = body.comments
                                val comma = ","
                                val commentArr = Pattern.compile(comma).split(comment)
                                commentsList.addAll(commentArr)
                                adapter.updateCommentsList(commentsList)
                                tvViewPostComments.text = "Comments: ${commentsList.size}"
                            }
                            else {
                                tvViewPostComments.text = "Comments: 0"
                            }

                            //Likes data and count
                            if (body.likes.isNotEmpty()){
                                val like = body.likes
                                val comma = ","
                                val likeArr = Pattern.compile(comma).split(like)
                                likesList.addAll(likeArr)
                                tvViewPostLikes.text = "Likes: ${likesList.size}"
                            }
                            else {
                                tvViewPostLikes.text = "Likes: 0"
                            }
                            tvViewPostTitle.text = body.title
                            tvViewPostText.text = body.text
                            oldPostData = body
                        }
                    }
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }


    //Update comment data
    private fun updatePostComments(){
        oldPostData.comments = "${oldPostData.comments}, ${etViewPostComment.text}"
        apiInterface?.updatePost(postId, oldPostData)
            ?.enqueue(object : Callback<PostsItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<PostsItem>,
                    response: Response<PostsItem>) {

                    if (response.body() != null) {
                        var body = response.body()
                        if (body != null)
                        {
                            Toast.makeText(this@ShowPostActivity,
                                "Comment has been added", Toast.LENGTH_SHORT).show()
                            getPost()
                        }
                    }
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }


    //Update Likes from API
    private fun updatePostLikes(){
        likesList.add(userLogin)
        oldPostData.likes = "${oldPostData.likes}, ${likesList.last()}"
        apiInterface?.updatePost(postId, oldPostData)
            ?.enqueue(object : Callback<PostsItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<PostsItem>,
                    response: Response<PostsItem>) {

                    if (response.body() != null) {
                        var body = response.body()
                        if (body != null)
                        {
                            Toast.makeText(this@ShowPostActivity,
                                "Like has been added", Toast.LENGTH_SHORT).show()
                            getPost()
                        }
                    }
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }
}