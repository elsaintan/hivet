package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.seaID.hivet.models.User

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore

    private lateinit var uName : TextView
    private lateinit var uPhoto : ImageView
    private lateinit var bLogout : ImageView
    private lateinit var bKonsul : ImageButton
    private lateinit var bBooking : ImageButton
    private lateinit var profile : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        val uId = mAuth.currentUser!!.uid

        setUser(uId)

        uName = findViewById(R.id.userName)
        uPhoto = findViewById(R.id.imageView3)
        bLogout = findViewById(R.id.imageLogout)
        bKonsul = findViewById(R.id.bkonsultasi)
        bBooking = findViewById(R.id.bBooking)
        profile = findViewById(R.id.imageSetting)

        bLogout.setOnClickListener {
            mAuth.signOut()
            //start login activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        bKonsul.setOnClickListener {
            val intent = Intent(this, ListDokterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("type", 1)
            startActivity(intent)
        }

        bBooking.setOnClickListener{
            startActivity(Intent(this, FilterBookingActivity::class.java))
        }


        profile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }


    }

    private fun setUser(id : String) {
        val uidRef  = mDbRef.collection("users").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                uName.text = "Hai "+user!!.name+"!"
                if (user!!.photoProfile == ""){
                    uPhoto.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(uPhoto)
                }
                //Toast.makeText(this, "{$user.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }


}