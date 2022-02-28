package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seaID.hivet.databinding.ActivityRatingBinding

class RatingActivity : AppCompatActivity() {
    
    private lateinit var rBinding: ActivityRatingBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rBinding = ActivityRatingBinding.inflate(layoutInflater)
        val view = rBinding.root
        setContentView(view)
        
        rBinding.ratingBar.rating = 2.5f
        rBinding.ratingBar.stepSize = .5f
        
        rBinding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->  }
        
        
    }
}