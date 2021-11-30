package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityMainBinding
import kotlin.math.log

class SignInActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var eEmail: EditText
    private lateinit var ePassword: EditText
    private lateinit var bLogin: Button
    private lateinit var bLoginGoogle: Button
    private lateinit var tvRegister: TextView
    private lateinit var tvFPassword : TextView
    private lateinit var mAuth: FirebaseAuth

    //constant
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        eEmail = findViewById(R.id.emialET)
        ePassword = findViewById(R.id.passwordET)
        bLogin = findViewById(R.id.loginBt)
        bLoginGoogle = findViewById(R.id.loginwGoogleBt)
        tvRegister = findViewById(R.id.registerTv)
        tvFPassword = findViewById(R.id.fpasswordTv)

        //configure the google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail() //only need email from google account
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google SignIn button, click to begin
        bLoginGoogle.setOnClickListener {
            Log.d(TAG, "onCreate: Begin Google Sign In")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }

        mAuth = FirebaseAuth.getInstance()

        // handle click begin login
        bLogin.setOnClickListener {
            //before login validate data
            validateData()

        }
        signup()
        forgotPassword()

    }

    private fun forgotPassword() {
        //code for jumping to signup
        tvRegister.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInAPI
        if (requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google SignIn success, now auth with firebase
                val account = accountTask.getResult(ApiException::class.java)
                mAuthWithGoogleAccount(account)
            }catch (e: Exception){
                //failed Google SignIn
                Log.d(TAG, "onActivityResult: ${e.message}" )
            }
        }
    }

    private fun mAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "mAuthWithGoogleAccount: begin firebase auth with google account")
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                //login success
                Log.d(TAG, "mAuthWuthFoogleAccount: LoggedIm")

                //get logged In User
                val firebaseUser = mAuth.currentUser

                //get user info
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email

                Log.d(TAG, "Id: $uid")
                Log.d(TAG, "Email: $email")

                //check if user is new or existing
                if (authResult.additionalUserInfo!!.isNewUser){
                    //user is new -- Account created
                    Log.d(TAG, "Account created")
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    //start prifle activity
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("Uid", uid)
                    startActivity(intent)
                    finish()
                }else{
                    //existing iser -- Logged In
                    Log.d(TAG, "Existing user... \n$email")
                    Toast.makeText(this, "Logged In... \n$email", Toast.LENGTH_SHORT).show()
                    toHome(uid)
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "mAuthWithGoogleAccount: Login Failed due to ${e.message}")
            }

    }

    private fun validateData() {
        //get data
        val email = eEmail.text.toString()
        val password = ePassword.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            Toast.makeText(this, "Invalid E-mail Format", Toast.LENGTH_SHORT).show()
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        }else{
            login(email, password)
        }


    }

    private fun login(email: String, password: String) {
//        logic for login user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //login success
                    //get logged In User
                    val firebaseUser = mAuth.currentUser

                    //get user info
                    val uid = firebaseUser!!.uid

                    //jumping to Home
                    toHome(uid)
                } else {
                    Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e->
                //login failed
                Toast.makeText( this, "Login failed due to " +e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun signup() {
        //code for jumping to signup
        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun toHome(uid : String){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("Uid", uid)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        //if user already logged in, go to Main Activity
        //get current user
        val currentUser = mAuth.currentUser
        if (currentUser != null){
            //user is already logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}