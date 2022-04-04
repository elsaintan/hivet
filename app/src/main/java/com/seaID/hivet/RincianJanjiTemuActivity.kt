package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.databinding.ActivityRincianJanjiTemuBinding
import com.seaID.hivet.models.User
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.drh

class RincianJanjiTemuActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    private lateinit var rjbinding : ActivityRincianJanjiTemuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rjbinding = ActivityRincianJanjiTemuBinding.inflate(layoutInflater)
        setContentView(rjbinding.root)

        mAuth = FirebaseAuth.getInstance()

        val id = intent.getStringExtra("id")

        showData(id)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showData(id: String?) {
        db = FirebaseFirestore.getInstance()
        db.collection("booking_appointments").document(id.toString())
            .get()
            .addOnSuccessListener {
                if(it != null){
                    val janjitemu = it.toObject(booking::class.java)
                    rjbinding.tanggalap.text = janjitemu!!.tanggal
                    rjbinding.status.text = janjitemu!!.status
                    rjbinding.waktu.text = janjitemu!!.waktu
                    rjbinding.buttonKode.text = janjitemu.kode_booking
                    showdrhdata(janjitemu.drh_id)
                    //showpetdata(janjitemu.pet_id)
                }
            }
    }

    private fun showdrhdata(userId: String?){
        val data1 = db.collection("drh").document(userId.toString())
        data1.get().addOnSuccessListener {
            val drh = it.toObject(drh::class.java)
            if (drh != null){
                rjbinding.nameTV.text = drh.Name
                rjbinding.tempatTV.text = drh.alamat
                rjbinding.tempatpraktik.text = drh.tempat
                rjbinding.tempatklinik.text = drh.alamat
                if (drh.photoProfile != null) {
                    rjbinding.photodrh.setImageResource(R.drawable.profile)
                } else {
                    Glide.with(this).load(drh.photoProfile).into(rjbinding.photodrh)
                }

            }
        }
    }

}