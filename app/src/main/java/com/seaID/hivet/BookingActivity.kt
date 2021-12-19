package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.seaID.hivet.models.User
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.peliharaan


class BookingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var binding: ActivityBookingBinding
    private lateinit var db : FirebaseFirestore
    val pets = ArrayList<String>()
    var text: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uId = intent.getStringExtra("Uid")
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()


        myPets()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pets)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mypetS.adapter = adapter
        binding.mypetS.onItemSelectedListener = this


        showdetailData(uId.toString())

    }

    private fun showdetailData(id : String) {
        val uidRef  = db.collection("drh").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(drh::class.java)
                binding.namedrhTV.setText(user!!.Name)
                binding.workexpTV.text = user!!.alamat
                binding.tanggalap.text = user!!.booking
                binding.tempatklinik.text = user!!.alamat
                if (user!!.photoProfile == ""){
                    binding.photodrh.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(binding.photodrh)
                }
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }

    private fun myPets(){

        db.collection("peliharaan")
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(peliharaan::class.java)
                val items = data.size
                if (items > 0){
                    for (item in data){
                        if (item.pemilik == mAuth.uid){
                            pets.add(item.nama!!)
                        }
                    }
                }
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        text= parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}