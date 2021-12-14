package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.seaID.hivet.databinding.ActivityUserProfileBinding
import com.seaID.hivet.models.drh

private lateinit var binding: ActivityBookingBinding
private lateinit var appointment : FirebaseFirestore

class BookingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uId = intent.getStringExtra("Uid")
        val name = intent.getStringExtra("Name")
        val profilePic = intent.getStringExtra("ProfilePic")

        binding.namedrhTV.setText(name)
        if (profilePic == ""){
            binding.photodrh.setImageResource(R.drawable.profile)
        }else{
            Glide.with(this).load(profilePic).into(binding.photodrh)
        }

        showdetailData(uId.toString())

    }

    private fun showdetailData(id : String) {
        appointment = FirebaseFirestore.getInstance()
        appointment.collection("appointment")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ){
                    if (error != null){
                        Log.e("Error: ", error.message.toString())
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){

                        }

                    }

                }
            })
    }
}