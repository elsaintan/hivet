package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityBookingBerhasilBinding
import com.seaID.hivet.databinding.ActivityUserProfileBinding
import com.seaID.hivet.models.User
import com.seaID.hivet.models.booking

class BookingBerhasilActivity : AppCompatActivity() {

    private lateinit var bBinding : ActivityBookingBerhasilBinding
    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bBinding = ActivityBookingBerhasilBinding.inflate(layoutInflater)
        val view = bBinding.root
        setContentView(view)

        mDbRef = FirebaseFirestore.getInstance()
        loadData(intent.getStringExtra("kode_booking"))


    }

    private fun loadData(kode_booking: String?) {

        val app  = mDbRef.collection("booking_appointments").document(kode_booking.toString())

        app.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val data = doc.toObject(booking::class.java)
                //mybinding.namauTV.text = user!!.name
                //mybinding.emailTV.text = user!!.email
                bBinding.kodeBooking.text = data!!.kode_booking
                bBinding.waktukonsul.text = data!!.waktu
                bBinding.tanggalap.text = data!!.tanggal
                //Log.d(UserProfileActivity.TAG, "{$user.name}")
            } else {
                //Log.d(UserProfileActivity.TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this,"get failed with "+ exception,  Toast.LENGTH_SHORT).show()
            //Log.d(UserProfileActivity.TAG, "get failed with " +exception,)
        }

    }
}