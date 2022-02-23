package com.seaID.hivet

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.seaID.hivet.databinding.ActivityKonsultasiBinding
import com.seaID.hivet.models.User
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.konsultasi
import com.seaID.hivet.models.peliharaan
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class KonsultasiActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityKonsultasiBinding
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    val pets = ArrayList<String>()
    var text: String ?= null
    var counter : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKonsultasiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val id = intent.getStringExtra("Uid")
        mAuth = FirebaseAuth.getInstance()

        showData(id.toString())
        dataPeliharaan()

        binding.konsulBT.setOnClickListener {
            //startActivity(Intent(this, ChatActivity::class.java))
            saveData()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        val id_drh = intent.getStringExtra("Uid")
        val id_user = mAuth.currentUser!!.uid
        val length = 8
        val id : String = getRandomString(length)
        val current = LocalDateTime.now()
        val simpleDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val formatted = current.format(simpleDateFormat)
        val konsultasi = konsultasi(id, id_drh, id_user, binding.peliharaanSP.selectedItem.toString(), formatted, "Menunggu Konfirmasi")
        mDbRef.collection("konsultasi").document(id).set(konsultasi)
            .addOnCompleteListener {

            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "Action failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }
    }
    override fun onResume() {
        super.onResume()
        mDbRef.collection("konsultasi")
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(konsultasi::class.java)
                val items = data.size
                if (items > 0) {
                    for (item in data) {
                        if (item.status == "Diterima") {
                            val intent = Intent(this, PaymentActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("nama_drh", binding.namadrhTV.text)
                            intent.putExtra("harga", binding.hargaTV.text)
                            intent.putExtra("Uid", item.id_drh)
                            intent.putExtra("id", item.id)
                            intent.putExtra("id_pet", item.id_pet)
                            intent.putExtra("tanggal", item.tanggal)
                            startActivity(intent)
                        }
                    }
                }
            }
    }


    private fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        counter++
        if (counter == 1){
            super.onBackPressed()
        }
    }

    private fun dataPeliharaan() {
        mDbRef.collection("peliharaan")
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
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pets)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.peliharaanSP.adapter = adapter
                binding.peliharaanSP.onItemSelectedListener = this
            }
    }


    private fun showData(id : String) {
        mDbRef = FirebaseFirestore.getInstance()
        val uidRef  = mDbRef.collection("drh").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(drh::class.java)
                binding.namadrhTV.text = user!!.Name
                binding.workExpTV.text = user!!.WorkExp
                binding.hargaTV.text = user!!.harga
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        text= parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}