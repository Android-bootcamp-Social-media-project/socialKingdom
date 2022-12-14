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
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_show_post.*
import retrofit2.*

class ShowPostActivity : AppCompatActivity() {

    private val apiInterface by lazy { APIClient().getClient()?.create(APIInterface::class.java) }

    // set default values
    var postId = 0
    var postImage = ""
    var postUser = ""
    var userLogin = "Anonymous"

    lateinit var userNamePost : TextView
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

        // initializing lateinit variable
        oldPostData = PostsItem("","","","","",0)


        //check if user login
        val tempUserLogin = intent.getStringExtra("userLogin")
        if (tempUserLogin != null){
            userLogin = tempUserLogin
        }

        postId = intent.getIntExtra("postId", 0)

        myRV = findViewById(R.id.rvViewPostComments)
        myRV.adapter = adapter
        myRV.layoutManager = LinearLayoutManager(this)

        userNamePost = findViewById(R.id.userNamePost)
        tvViewPostTitle = findViewById(R.id.tvViewPostTitle)
        tvViewPostText = findViewById(R.id.tvViewPostText)
        tvViewPostLikes = findViewById(R.id.tvViewPostLikes)
        tvViewPostComments = findViewById(R.id.tvViewPostComments)

        etViewPostComment = findViewById(R.id.etViewPostComment)
        btLeaveComment = findViewById(R.id.btLeaveComment)
        btLike = findViewById(R.id.btLike)

        getPost()
        btLeaveComment.setOnClickListener{
            if (etViewPostComment.text.isNotEmpty()){
                if (userLogin == "Anonymous"){
                    Toast.makeText(this,"You should be login", Toast.LENGTH_SHORT).show()
                }
                else {
                        updatePostComments()
                        etViewPostComment.clearFocus()
                        etViewPostComment.text.clear()
                    }
            }
            else {
                Toast.makeText(this@ShowPostActivity,
                    "All field is required", Toast.LENGTH_SHORT).show()
            }
        }

        btLike.setOnClickListener {
            if(userLogin != "Anonymous") {
                if (likesList.contains(userLogin)) {
                    Toast.makeText(this, "You have already liked this post", Toast.LENGTH_LONG).show()
                }
                else{
                    updatePostLikes()
                }
            } else {
                Toast.makeText(this, "You should login", Toast.LENGTH_LONG).show()
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
                        //try{

                            val comment = body.comments
                            comments(comment)
                            //Comments data and count


                        likes(body.likes)

                        tvViewPostTitle.text = body.title
                        tvViewPostText.text = body.text
                        oldPostData = body
                        tvViewPostComments.text = "Comments: ${comments(comment)}"
                        tvViewPostLikes.text = "Likes: ${likes(body.likes)}"

                        //Check and store userName post
                        postUser = body.user
                        getAllUsers()
                    }
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }

    private fun likes(likesString: String): Int {

        if(likesString.isNotEmpty()){return likesString.split(",").size}
        return 0

    }
    private fun comments(commentsString: String): Int {

        if (commentsString.isNotEmpty()){

            commentsList = commentsString.split(",") as ArrayList<String>
            val newCommentsList = ArrayList<String>()

            for(comment:String in commentsList){
                newCommentsList.add(comment)
            }

            commentsList = newCommentsList
            Log.d("TAG", "onResponse: ${commentsList.toString()}")

            adapter.updateCommentsList(commentsList)
            return commentsList.size
        }
        return 0
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
                            Toast.makeText(this@ShowPostActivity,"Comment has been added", Toast.LENGTH_SHORT).show()
                            getPost()
                            Log.d("TAG", "ON updatepostcomment :::: $likesList")

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

                override fun onResponse( call: Call<PostsItem>,  response: Response<PostsItem>) {

                    var body = response.body()
                        if (body != null) {
                            Toast.makeText(this@ShowPostActivity, "Like has been added",Toast.LENGTH_SHORT).show()
                            getPost()
                        }
                }

                override fun onFailure(call: Call<PostsItem>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }

    //Request user data from API
    private fun getAllUsers(){
        apiInterface?.getAllUsers()?.enqueue(object : Callback<Users> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if (response.body() != null) {
                        var body = response.body()

                        if (body != null) {
                            for (i in 0 until body.size) {
                                if (body[i].username == postUser){
                                    postImage = body[i].image
                                    break
                                }
                            }
                            Log.d("PostImageInfo", "postImage: $postImage")
                            getImage()
                        }
                    }
                }
                override fun onFailure(call: Call<Users>, t: Throwable) {
                    Log.d("Main", "onFailure: ${t.message.toString()}")
                }
            })
    }

    fun getImage() {
        try {
            if (postImage.isNotEmpty()) {
                Glide.with(this)
                    .load(postImage)
                    .override(600, 200)
                    .into(imageView2)
            } else {
                postImage = "https://i.ibb.co/71wByPN/1ee67050-845a-4c89-8032-172dc0d14b00.jpg"
                Glide.with(this)
                    .load(postImage)
                    .override(600, 200)
                    .into(imageView2)
            }
            userNamePost.text = postUser
        } catch (e:Exception){
            Log.d("Catch", "No image: $e")
        }
    }
}