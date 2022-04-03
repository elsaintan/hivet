package com.seaID.hivet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.databinding.ActivityPeliharaanBinding
import com.seaID.hivet.models.peliharaan

class PeliharaanActivity : AppCompatActivity() {

    private lateinit var petbinding : ActivityPeliharaanBinding
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petbinding = ActivityPeliharaanBinding.inflate(layoutInflater)
        val view = petbinding.root
        setContentView(view)

        mDbRef = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        var type = intent.getIntExtra("type", 0)
        var id = intent.getStringExtra("pemilik")
        var nama = intent.getStringExtra("nama")
        var jen = intent.getStringExtra("jenis")
        var ket = intent.getStringExtra("ket")


        if (type == 2){
            petbinding.namaHET.setText(nama)
            petbinding.jenisET.setText(jen)
            petbinding.ketET.setText(ket)
        }

        var name = petbinding.namaHET.text
        var jenis = petbinding.jenisET.text
        var keterangan = petbinding.ketET.text

        petbinding.saveBT.setOnClickListener {
            saveData(mAuth.currentUser!!.uid, name.toString(), jenis.toString(), keterangan.toString())
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, UserProfileActivity::class.java))
        finish()
    }

    private fun saveData(idp : String, name : String, jenis : String, keterangan : String) {
        val type = intent.getIntExtra("type", 0)
        val idd = intent.getStringExtra("petid")
        var id = ""
        if(type.toString() == "2"){
            id = idd.toString()
        }else{
            id = mDbRef.collection("peliharaan").document().getId()
        }

        Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

        val peliharaan = peliharaan(id, idp, name, jenis, keterangan)
        mDbRef.collection("peliharaan").document(id).set(peliharaan)
            .addOnCompleteListener {
                Toast.makeText(this, "OKE", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserProfileActivity::class.java))
            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "Action failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }


    }
}