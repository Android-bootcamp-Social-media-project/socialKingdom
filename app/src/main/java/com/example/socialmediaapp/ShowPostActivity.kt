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
import retrofit2.*

class ShowPostActivity : AppCompatActivity() {

    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }
    //val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
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
        val tempUserLogin = intent.getStringExtra("userLogin")
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

            if(userLogin != "Anonymous") {
                if (likesList.contains(userLogin!!)) {
                    Toast.makeText(this, "You have already liked this post", Toast.LENGTH_LONG)
                        .show()
                }
                else{
                    updatePostLikes()
                }
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
                        var body = response.body()!!
                        try{
                            //Comments data and count
                            if (body.comments.isNotEmpty()){
                                val comment = body.comments
                               // val comma = ","
                                commentsList = comment.split(",") as ArrayList<String>
                                val newCommentsList = ArrayList<String>()

                                // val commentArr = Pattern.compile(comma).split(comment)
                                for(comment:String in commentsList){
                                    newCommentsList.add(comment)
                                }
                                //commentsList.addAll(commentArr)
                                commentsList = newCommentsList
                                Log.d("TAG", "onResponse: ${commentsList.toString()}")
                                adapter.updateCommentsList(commentsList)
                                tvViewPostComments.text = "Comments: ${commentsList.size}"
                            }
                            else {
                                tvViewPostComments.text = "Comments: 0"
                            }
                        }catch (e:Exception){
                            Log.d("TAG_SHOW_POST_ACTIVITY", "Exception in comments: $e")
                        }
                        try {
                            //Likes data and count
                            if (body.likes.isNotEmpty()) {
                                val like = body.likes

                                likesList = like.split(",") as ArrayList<String>

                                val newLikesList = ArrayList<String>()

                                for(likes:String in likesList){
                                    newLikesList.add(likes)
                                }
                                likesList = newLikesList

                                Log.d("TAG", "ON LIKES: ${likesList.toString()}")
                                tvViewPostLikes.text = "Likes: ${likesList.size}"
                            } else {
                                tvViewPostLikes.text = "Likes: 0"
                            }
                            tvViewPostTitle.text = body.title
                            tvViewPostText.text = body.text
                            oldPostData = body
                        }catch (e:Exception){
                            Log.d("TAG_SHOW_POST_ACTIVITY", "Exception in likes: $e")
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
                        if (body != null){
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
/*        for (i in 0 until likesList.size) {
            if (likesList[i] != userLogin) {
                Log.d("TAG", "updatePostLikes: $userLogin")
                Toast.makeText(this@ShowPostActivity, "FROM LIKES: Like has been added!!", Toast.LENGTH_SHORT).show()
                break
            }

        }*/
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