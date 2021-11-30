package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore

    private lateinit var uName : TextView
    private lateinit var uPhoto : ImageView
    private lateinit var bLogout : ImageView

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

        uName.text = "Id :  $uId"

        bLogout.setOnClickListener {
            mAuth.signOut()
            //start login activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun setUser(id : String) {
        
    }


}