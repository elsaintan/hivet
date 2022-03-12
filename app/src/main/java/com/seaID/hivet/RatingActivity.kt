package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityRatingBinding
import com.seaID.hivet.models.Rating
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.konsultasi

class RatingActivity : AppCompatActivity() {
    
    private lateinit var rBinding: ActivityRatingBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore
    private var rate : Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rBinding = ActivityRatingBinding.inflate(layoutInflater)
        val view = rBinding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()
        
        rBinding.ratingBar.rating = 2.5f
        rBinding.ratingBar.stepSize = .5f
        
        rBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            rate = rating
        }
        
        addRating(rate)
    }

    private fun addRating(rate: Float) {
        val user_id = mAuth.currentUser?.uid
        val drh_id = intent.getStringExtra("drh_id")
        val konsul_id = intent.getStringExtra("id_konsul")

        val rating = Rating(konsul_id, drh_id, user_id, rate)
        mDbRef.collection("rating").document(konsul_id.toString()).set(rating)
            .addOnCompleteListener {
                toHistory()
            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "Action failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun toHistory() {

    }


}