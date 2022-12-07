package com.example.socialmediaapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import retrofit2.*
import java.util.concurrent.Executor

class LoginPageActivity : AppCompatActivity() {

    val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)

    // Set user default value as anonymous
    var userLogin = "anonymous"


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
    lateinit var sharedPreferences: SharedPreferences

    var username = ""
    var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


         sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
         username = sharedPreferences.getString("User", "")!!
         password = sharedPreferences.getString("Pass", "")!!

        if (username!=""&&password!="") {
            //showBiometricPromptForDecryption()
        }


        // Sign up variables
        userNameEtSignup = findViewById(R.id.userNameEtSignup)
        emailEtSignUp = findViewById(R.id.emailEtSignUp)
        userPasswordEtsignup = findViewById(R.id.userPasswordEtsignup)
        imageURLEt = findViewById(R.id.imageURLEt)
        signUpBtn = findViewById(R.id.signUpBtn)

        signUpBtn.setOnClickListener {

            if (userNameEtSignup.text.isNotEmpty() && emailEtSignUp.text.isNotEmpty() && userPasswordEtsignup.text.isNotEmpty()) {
                // addUser()

                if (userNameEtSignup.text.isNotEmpty() &&
                    emailEtSignUp.text.isNotEmpty() &&
                    userPasswordEtsignup.text.isNotEmpty()
                ) {
                    //Check if the user has inserted the image
                    if (imageURLEt.text.isNotEmpty()) {
                        userImage = imageURLEt.text.toString()
                        //addUser()
                    } else {
                        //addUser()
                    }

                } else {
                    Toast.makeText(
                        this@LoginPageActivity,
                        "All field are required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //Login variable
        userNameEtsignup = findViewById(R.id.userNameEtLogin)
        passwordEt = findViewById(R.id.passwordEt)
        logInBtn = findViewById(R.id.logInBtn)

        logInBtn.setOnClickListener {

            if (userNameEtSignup.text.isNotEmpty() && userPasswordEtsignup.text.isNotEmpty()){
                //loginUser()
            }
           // showBiometricPromptForDecryption()
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

        if (username == "" && password == ""){
            username = userNameEtsignup.text.toString()
            password = passwordEt.text.toString()
        }

        apiInterface?.login( username,password )
            ?.enqueue(object : Callback<String> {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.body() != null) {

                        // Defining variable for storing
                        var key = response.body()
                        Log.d("APIKey", "$key")

                        apiKey = key.toString()
                        if (apiKey != "")
                        {
                            sharedPreferences.edit().apply {

                                putString("User",username)
                                putString("Pass",password)
                                apply()

                            }
                           // getUserData()
                            Toast.makeText(this@LoginPageActivity,"Welcome back $username", Toast.LENGTH_SHORT).show()
                        }


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
                            //homePageActivity()
                        }
                    }
                }
                override fun onFailure(call: Call<UsersItem>, t: Throwable) {
                    Log.d("MAIN", "onFailure: ${t.message.toString()}")
                }
            })
    }

/*    private fun homePageActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userLogin",userLogin)
        startActivity(intent)
    }*/

 ////////////////////////////////////////////////////////////////
 //////////////////Fingerprint from user/////////////////////////
////////////////////////////////////////////////////////////////
/*
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private fun showBiometricPromptForDecryption() {
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("FingerPrint Sensor")
            .setSubtitle("Log in using your fingerprint")
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()


        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Fingerprint doesn't match: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                // On successful authentication
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    // Print succession message with toast message
                    Toast.makeText(applicationContext,"Successfully Authenticated", Toast.LENGTH_SHORT) .show()

                        loginUser()
                }

                // On authentication failed
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    // Print error message with toast message
                    Toast.makeText(applicationContext, "Authentication Failed!!",Toast.LENGTH_SHORT).show()
                }
            })

        // Initializing biometricManager
        val biometricManager = BiometricManager.from(this)

        // Fingerprint authentication starts here..
        when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {

            BiometricManager.BIOMETRIC_SUCCESS -> Log.d("MY_APP_TAG", "App can authenticate using biometrics.")

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> Log.e("MY_APP_TAG", "No biometric features available on this device.")

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {

                // Prompts the user to create credentials that your app accepts.
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollIntent, 1)
            }
        }

        biometricPrompt.authenticate(promptInfo)

    }*/
}