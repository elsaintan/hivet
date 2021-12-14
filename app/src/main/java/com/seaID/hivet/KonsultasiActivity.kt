package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.seaID.hivet.databinding.ActivityKonsultasiBinding
import com.seaID.hivet.models.User

private lateinit var binding: ActivityKonsultasiBinding
private lateinit var mDbRef: FirebaseFirestore

class KonsultasiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonsultasiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val id = intent.getStringExtra("Uid")

        showData(id.toString())
    }

    private fun showData(id : String) {
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
                Toast.makeText(this, "{$user.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }
}