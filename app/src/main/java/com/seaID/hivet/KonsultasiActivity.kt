package com.seaID.hivet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.Window
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
    var text: String ? = null
    var counter : Int = 0
    var idk: String ? =null
    var isRunning: Boolean = false;
    lateinit var countdown_timer: CountDownTimer

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
        val konsultasi = konsultasi(id, id_drh, id_user, binding.peliharaanSP.selectedItem.toString(), formatted, "1")
        mDbRef.collection("konsultasi").document(id).set(konsultasi)
            .addOnCompleteListener {
                startTimer(120000, id)
            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "Action failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun startTimer(time_in_seconds: Long, id : String) {
        countdown_timer = object: CountDownTimer(time_in_seconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                cekStatus(id)
                showDialog()
            }

            override fun onFinish() {

            }
        }
        countdown_timer.start()

        isRunning = true

    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.popup_notif_konsul)
        if(isRunning == false){
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun changeStatus() {
        Toast.makeText(applicationContext, "Permintaan Konsultasi Dibatalkan", Toast.LENGTH_SHORT).show()
    }

    private fun cekStatus(id: String) {

            val data = mDbRef.collection("konsultasi").document(id)
            data.get().addOnSuccessListener {
                val data = it.toObject(konsultasi::class.java)
                if (data != null) {
                    if (data.id == id && data.status == "2"){
                        toPayment(data.id_drh, data.id, data.id_pet, data.tanggal)
                        countdown_timer.cancel()
                        isRunning = false
                    }else if(data.id == idk && data.status == "5"){
                        Toast.makeText(applicationContext, "Permintaan Konsultasi Ditolak", Toast.LENGTH_SHORT).show()
                        countdown_timer.cancel()
                        isRunning = false

                    }

                }
            }

                //val data =
                //val data = it.toObjects(konsultasi::class.java)
                //val items = data.size
                //if (items > 0) {
                //    for (item in data) {
                //        if (item.id == idk && item.status == "Diterima") {
                //            toPayment(item.id_drh, item.id, item.id_pet, item.tanggal)
                //        }
                //    }
                //}

    }

    private fun toPayment(idDrh: String?, id: String?, idPet: String?, tanggal: String?) {
        val intent = Intent(this, KonsulPaymentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("nama_drh", binding.namadrhTV.text)
        intent.putExtra("harga", binding.hargaTV.text)
        intent.putExtra("Uid", idDrh)
        intent.putExtra("id", id)
        intent.putExtra("id_pet", idPet)
        intent.putExtra("tanggal", tanggal)
        startActivity(intent)
    }

    /**override fun onResume() {
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
    }**/


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