package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityBookingBerhasilBinding
import com.seaID.hivet.databinding.ActivityUserProfileBinding
import com.seaID.hivet.models.User
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.drh

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

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun loadData(kode_booking: String?) {

        val db = FirebaseDatabase.getInstance().getReference("booking_appointments")
        db.child(kode_booking.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data : booking? = snapshot.getValue(booking::class.java)
                    if (data != null) {
                        showdrh(data.drh_id)
                        bBinding.kodeBooking.text = data.kode_booking
                        bBinding.waktukonsul.text = data.waktu
                        bBinding.tanggalap.text = data.tanggal
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })

    }

    private fun showdrh(drhId: String?) {

        val db = FirebaseDatabase.getInstance().getReference("drh")
        db.child(drhId.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data : drh? = snapshot.getValue(drh::class.java)
                    if (data != null) {
                        bBinding.namedrhTV.text = data.Name
                        bBinding.tempatPraktikTV.text = data.tempat
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BookingBerhasilActivity,"get failed with "+ error,  Toast.LENGTH_SHORT).show()
                }

            })
    }
}