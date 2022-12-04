package com.seaID.hivet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.databinding.ActivityPeliharaanBinding
import com.seaID.hivet.models.Chat
import com.seaID.hivet.models.peliharaan

class PeliharaanActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var petbinding : ActivityPeliharaanBinding
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    var jenis : String ?= null

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
            petbinding.ketET.setText(ket)
        }

        var name = petbinding.namaHET.text
        var keterangan = petbinding.ketET.text
        var beratbadan = petbinding.beratbadanET.text

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.jenis_hewan, android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petbinding.jenisET.adapter = adapter
        petbinding.jenisET.onItemSelectedListener = this

        if (jen != null){
            val spinnerPosition : Int = adapter.getPosition(jen)
            petbinding.jenisET.setSelection(spinnerPosition)
        }

        petbinding.saveBT.setOnClickListener {
            validation(name.toString(), keterangan.toString(), beratbadan.toString())

        }
    }

    private fun validation(name: String?, keterangan: String?, beratbadan: String?) {
        if (name != "" && keterangan != ""){
            saveData(mAuth.currentUser!!.uid, name!!, jenis.toString(), beratbadan.toString(),keterangan!!)
        }else{
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onBackPressed() {
        startActivity(Intent(this, UserProfileActivity::class.java))
        finish()
    }

    private fun saveData(idp : String, name : String, jenis : String, beratbadan: String?, keterangan : String) {
        val type = intent.getIntExtra("type", 0)
        val idd = intent.getStringExtra("petid")
        var id = ""
        if(type.toString() == "2"){
            id = idd.toString()
        }else{
            id = mDbRef.collection("peliharaan").document().getId()
        }

        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

        val peliharaan = peliharaan(id, jenis, keterangan, name, mAuth.currentUser!!.uid)
        val reference = FirebaseDatabase.getInstance().reference
        reference.child("peliharaan").child(id).setValue(peliharaan)
            .addOnFailureListener {
                throw it
            }.addOnSuccessListener{
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, UserProfileActivity::class.java))
                finish()
            }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val text: String = parent?.getItemAtPosition(position).toString()
        jenis = text
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}