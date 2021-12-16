package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore

    private lateinit var uName : TextView
    private lateinit var uPhoto : ImageView
    private lateinit var bLogout : ImageView
    private lateinit var bKonsul : ImageButton
    private lateinit var profile : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uId = intent.getStringExtra("Uid")


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()


        //setUser(uId.toString())

        uName = findViewById(R.id.userName)
        uPhoto = findViewById(R.id.imageView3)
        bLogout = findViewById(R.id.imageLogout)
        bKonsul = findViewById(R.id.bkonsultasi)
        profile = findViewById(R.id.imageSetting)

        uName.text = "Id :  $uId"

        bLogout.setOnClickListener {
            mAuth.signOut()
            //start login activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        bKonsul.setOnClickListener {
            startActivity(Intent(this, ListDokterActivity::class.java))
        }

        profile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }


    }

    private fun setUser(id : String) {
        
    }


}