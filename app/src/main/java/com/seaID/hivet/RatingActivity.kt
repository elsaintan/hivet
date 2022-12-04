package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Value
import com.seaID.hivet.databinding.ActivityRatingBinding
import com.seaID.hivet.models.Rating
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.konsultasi
import java.util.HashMap

class RatingActivity : AppCompatActivity() {
    
    private lateinit var rBinding: ActivityRatingBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore
    var reference: DatabaseReference? = null
    private var rate : Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rBinding = ActivityRatingBinding.inflate(layoutInflater)
        val view = rBinding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()
        
        rBinding.ratingBar.rating = 0.0f
        rBinding.ratingBar.stepSize = .5f
        
        rBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rate = rating

        }

        rBinding.button6.setOnClickListener {
            addRating(rate)
            //Toast.makeText(this, "Rate "+rate, Toast.LENGTH_SHORT).show()
        }

        showData()

    }

    private fun showData() {
        val userid = intent.getStringExtra("drh_id")

        val reference = FirebaseDatabase.getInstance().getReference("drh")
        reference.child(userid.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val drh: drh? = snapshot.getValue(drh::class.java)
                    if (drh != null) {
                        rBinding.labelNama.text = drh!!.Name
                        if (drh!!.photoProfile == ""){
                            rBinding.foto.setImageResource(R.drawable.profile)
                        }else{
                            Glide.with(this@RatingActivity).load(drh!!.photoProfile).into(rBinding.foto)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }



    private fun addRating(rate: Float) {

        val id = intent.getStringExtra("konsul_id")
        val reference = FirebaseDatabase.getInstance().getReference("konsultasi")
        reference.child(id.toString()).child("rating").setValue(rate.toString())
            .addOnSuccessListener {
                toHistory()
            }
            .addOnFailureListener {
                throw it
            }

    }

    private fun toHistory() {
        startActivity(Intent(this, RiwayatLayoutActivity::class.java))
        finish()
    }


}