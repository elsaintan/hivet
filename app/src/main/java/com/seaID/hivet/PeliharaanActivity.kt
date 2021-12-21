package com.seaID.hivet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        var name = petbinding.namaHET.text
        var jenis = petbinding.jenisET.text
        var keterangan = petbinding.ketET.text

        petbinding.saveBT.setOnClickListener {
            saveData(mAuth.currentUser!!.uid, name.toString(), jenis.toString(), keterangan.toString())
        }
    }

    private fun saveData(id : String, name : String, jenis : String, keterangan : String) {
        val peliharaan = peliharaan(id, name, jenis, keterangan)
        val id: String = mDbRef.collection("peliharaan").document().getId()
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