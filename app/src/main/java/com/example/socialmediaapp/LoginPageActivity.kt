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
import retrofit2.*

class LoginPageActivity : AppCompatActivity() {

    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

    // Set user as a default as anonymous
    var userLogin = "Anonymous"
    var apiKey = ""
    var userImage = "https://i.ibb.co/71wByPN/1ee67050-845a-4c89-8032-172dc0d14b00.jpg"

    lateinit var userNameEtSignup : EditText
    lateinit var emailEtSignUp : EditText
    lateinit var userPasswordEtsignup : EditText
    lateinit var imageURLEt : EditText
    lateinit var signUpBtn : Button

    lateinit var userNameEtsignup : EditText
    lateinit var passwordEt : EditText
    lateinit var logInBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


        // Sign up variables
        userNameEtSignup = findViewById(R.id.userNameEtSignup)
        emailEtSignUp = findViewById(R.id.emailEtSignUp)
        userPasswordEtsignup = findViewById(R.id.userPasswordEtsignup)
        imageURLEt = findViewById(R.id.imageURLEt)
        signUpBtn = findViewById(R.id.signUpBtn)

        signUpBtn.setOnClickListener {
            if (userNameEtSignup.text.isNotEmpty() &&
                emailEtSignUp.text.isNotEmpty() &&
                userPasswordEtsignup.text.isNotEmpty()) {
                //Check if the user has inserted the image
                if (imageURLEt.text.isNotEmpty()){
                    userImage = imageURLEt.text.toString()
                    addUser()
                }
                else {
                    addUser()
                }
            }
            else {
                Toast.makeText(this@LoginPageActivity,"All field is required", Toast.LENGTH_SHORT).show()
            }
        }


        //Login variable
        userNameEtsignup = findViewById(R.id.userNameEtLogin)
        passwordEt = findViewById(R.id.passwordEt)
        logInBtn = findViewById(R.id.logInBtn)

        logInBtn.setOnClickListener {
            if (userNameEtsignup.text.isNotEmpty() &&
                passwordEt.text.isNotEmpty()) {
                loginUser()
            }
            else {
                Toast.makeText(this@LoginPageActivity,"All field is required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //SignUp request
    private fun addUser(){
        apiInterface?.addUsers(
            UsersItem(
                emailEtSignUp.text.toString(),
                userNameEtSignup.text.toString(),
                userPasswordEtsignup.text.toString(),
                userImage
            )
        )
            ?.enqueue(object : Callback<UsersItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<UsersItem>,
                    response: Response<UsersItem>) {
                    Toast.makeText(this@LoginPageActivity,
                        "The user has been added successfully,  Please login..", Toast.LENGTH_LONG).show()
                }
                override fun onFailure(call: Call<UsersItem>, t: Throwable) {
                    Log.d("MAIN", "Something went wrong!")
                }
            })
    }


    //Get apiKey
    private fun loginUser(){
        apiInterface?.login(userNameEtsignup.text.toString() , passwordEt.text.toString())
            ?.enqueue(object : Callback<String> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.body() != null) {
                        var key = response.body()
                        Log.d("APIKey", "$key")

                        apiKey = key.toString()
                        Toast.makeText(this@LoginPageActivity,"logging successfully", Toast.LENGTH_SHORT).show()
                        getUserData()
                    }
                    else {
                        Toast.makeText(this@LoginPageActivity,"username or password not correct", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("MAIN", "Something went wrong!")
                }
            })
    }


    //Get user data by id
    private fun getUserData(){
        apiInterface?.getUserId(apiKey)
            ?.enqueue(object : Callback<UsersItem> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(
                    call: Call<UsersItem>,
                    response: Response<UsersItem>) {

                    if (response.body() != null) {
                        var body = response.body()
                        if (body != null)
                        {
                            userLogin = body.username
                            homePageActivity()
                        }
                    }
                }
                override fun onFailure(call: Call<UsersItem>, t: Throwable) {
                    Log.d("MAIN", "onFailure: ${t.message.toString()}")
                }
            })
    }

    private fun homePageActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }
}