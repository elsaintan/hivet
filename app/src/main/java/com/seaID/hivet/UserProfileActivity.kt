package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.`class`.User
import com.seaID.hivet.databinding.ActivityEditProfileBinding
import com.seaID.hivet.databinding.ActivityMainBinding
import com.seaID.hivet.databinding.ActivityUserProfileBinding

class UserProfileActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityUserProfileBinding

    //firebase auth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: FirebaseFirestore

    //constant
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //init firebasee auth
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        val currentUser = mAuth.currentUser

        loadProfile(currentUser?.uid!!)

    }

    private fun loadProfile(id : String) {
        val uidRef  = mDbRef.collection("users").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                binding.namauTV.text = user!!.name
                binding.emailTV.text = user!!.email
                if (user!!.photoProfile == ""){
                    binding.profileIM.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(binding.profileIM)
                }
                Log.d(TAG, "{$user.name}")
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }



    }


}