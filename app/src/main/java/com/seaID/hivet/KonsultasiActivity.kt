package com.seaID.hivet

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.seaID.hivet.databinding.ActivityKonsultasiBinding
import com.seaID.hivet.models.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class KonsultasiActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityKonsultasiBinding
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var mAuth : FirebaseAuth
    var reference: DatabaseReference? = null
    val pets = ArrayList<String>()
    val idpets = ArrayList<String>()
    var text: String ? = null
    var counter : Int = 0
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

            if (binding.peliharaanSP.isNotEmpty()){
                saveData()
            }else{
                addPets()
            }

        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, ListDokterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("type", 1)
        startActivity(intent)
        finish()
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
        val idpet = idpets.get(binding.peliharaanSP.selectedItemId.toInt())
        val harga = binding.hargaTV.text.toString()
        //Toast.makeText(this, "Ini " +idpet, Toast.LENGTH_SHORT).show()

        val reference = FirebaseDatabase.getInstance().getReference()
        val konsultasi = konsultasi(id, id_drh, id_user, idpet, formatted, "1", "", harga, "")

        reference.child("konsultasi").child(id).setValue(konsultasi)
            .addOnSuccessListener {
                startTimer(120000, id)
                showDialog()
            }
            .addOnFailureListener {
                Toast.makeText(this, " "+it, Toast.LENGTH_SHORT).show()
            }
    }

    private fun addPets(){
        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this, UserProfileActivity::class.java))
            finish()
        }, 2000)
    }

    private fun startTimer(time_in_seconds: Long, id : String) {
        countdown_timer = object: CountDownTimer(time_in_seconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                cekStatus(id)
            }

            override fun onFinish() {
                changeStatus(id)
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


    private fun changeStatus(id : String) {
        //val id = intent.getStringExtra("Uid")
        val reference = FirebaseDatabase.getInstance().getReference("konsultasi")
        reference.child(id).child("status").setValue("6")
    }

    private fun cekStatus(id: String) {

        //val data = mDbRef.collection("konsultasi").document(id)
        FirebaseDatabase.getInstance().getReference("konsultasi").child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data: konsultasi? = snapshot.getValue(konsultasi::class.java)
                    if (data != null) {
                        if (data.id == id && data.status == "2"){
                            toPayment(data.id_drh, data.id, data.id_pet, data.tanggal, data.harga!!.toDouble())
                            countdown_timer.cancel()
                            isRunning = false
                        }else if(data.id == id && data.status == "5"){
                            Toast.makeText(applicationContext, "Permintaan Konsultasi Ditolak", Toast.LENGTH_SHORT).show()
                            countdown_timer.cancel()
                            isRunning = false

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })

    }

    private fun toPayment(idDrh: String?, id: String?, idPet: String?, tanggal: String?, harga: Double?) {
        //Toast.makeText(this, "ini "+id, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, KonsulPaymentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("nama_drh", binding.namadrhTV.text)
        intent.putExtra("harga", binding.hargaTV.text)
        intent.putExtra("Uid", idDrh)
        intent.putExtra("id", id)
        intent.putExtra("id_pet", idPet)
        intent.putExtra("tanggal", tanggal)
        intent.putExtra("harga", harga)
        startActivity(intent)
    }


    private fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun dataPeliharaan() {
        val ref = FirebaseDatabase.getInstance().getReference("peliharaan")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pets)
        ref.orderByChild("pemilik")
            .equalTo(mAuth.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snapshot in snapshot.children) {
                        val item = snapshot.getValue(peliharaan::class.java)
                            idpets.add(item?.id.toString())
                            pets.add(item?.nama.toString())
                    }

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.peliharaanSP.adapter = adapter
                    binding.peliharaanSP.onItemSelectedListener = this@KonsultasiActivity
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }


    private fun showData(id : String) {
        mDbRef = FirebaseFirestore.getInstance()
        val uidRef  = mDbRef.collection("drh").document(id)

        val ref = FirebaseDatabase.getInstance().getReference("drh")
        ref.child(id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: drh? = snapshot.getValue(drh::class.java)
                    if (user != null) {
                        binding.namadrhTV.text = user!!.Name
                        binding.workExpTV.text = user!!.harga
                        binding.hargaTV.text = user!!.harga
                        binding.textView.text =  user!!.STR
                        binding.exp.text = user.WorkExp+" tahun"

                        if (user!!.photoProfile == ""){
                            binding.profileIM.setImageResource(R.drawable.profile)
                        }else{
                            Glide.with(this@KonsultasiActivity).load(user!!.photoProfile).into(binding.profileIM)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //text= parent?.getItemAtPosition(position).toString()
        text = idpets.get(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}