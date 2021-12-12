package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.`class`.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var eName: EditText
    private lateinit var eEmail: EditText
    private lateinit var ePassword: EditText
    private lateinit var bSignup: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()
//        mDbRef = FirebaseDatabase.getInstance().getReference()

        eName = findViewById(R.id.nameET)
        eEmail = findViewById(R.id.emailET)
        ePassword = findViewById(R.id.passwordET)
        bSignup = findViewById(R.id.registerBt)

        bSignup.isEnabled = false

        ePassword.addTextChangedListener(textWatcher)

        bSignup.setOnClickListener {
            val name = eName.text.toString()
            val email = eEmail.text.toString()
            val password = ePassword.text.toString()

            register(name, email, password)

//            addUserToDatabase(name, email, "001")
        }
    }

    private fun register(name: String, email: String, password: String) {

//        locic of creating user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //code for jumping to home
                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!)
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Some error occurred " + task.exception,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String) {

        mDbRef = FirebaseFirestore.getInstance()

        val user = User(name, email, uid, null)


        val usersRef = mDbRef.collection("users")
        usersRef.document(uid).set(user)
            .addOnCompleteListener {
                Toast.makeText(this, "OKE", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "SignUp failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }

    }

    private val textWatcher = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            bSignup.isEnabled = true
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

}