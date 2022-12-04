package com.seaID.hivet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.UIKitCustomSetting
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.snap.Gopay
import com.midtrans.sdk.corekit.models.snap.Shopeepay
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.seaID.hivet.databinding.ActivityBookingBinding
import com.midtrans.sdk.corekit.BuildConfig.BASE_URL

import com.google.android.gms.common.internal.service.Common.CLIENT_KEY
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seaID.hivet.models.*
import java.util.*

class BookingActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    private lateinit var mAuth : FirebaseAuth
    private lateinit var binding: ActivityBookingBinding
    private lateinit var db : FirebaseFirestore
    val pets = ArrayList<String>()
    var text: String ?= null
    var counter : Int = 0
    private var transactionResult = TransactionResult()
    private var totalProduct: Int = 123456;
    val idpets = java.util.ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uId = intent.getStringExtra("Uid")
        val nama = intent.getStringExtra("Name")
        binding.tempatklinik.isVisible = false
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        myPets()
        showJadwal(uId.toString())
        showdetailData(uId.toString())


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), 101)
        }

        //initMidtransSdk()
        binding.reservasiBt.setOnClickListener {
            val intent = Intent(this, BookingPaymentActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("userid", mAuth.currentUser!!.uid)
            intent.putExtra("pet", idpets.get(binding.mypetS.selectedItemId.toInt()))
            intent.putExtra("drh", uId)
            intent.putExtra("name", nama)
            intent.putExtra("tanggal", binding.tanggalap.text)
            intent.putExtra("slot", "")
            startActivity(intent)
        }
    }

    private fun showJadwal(uid: String) {

        val reference = FirebaseDatabase.getInstance().getReference()
        reference.child("janjiTemu").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data= snapshot.getValue(jadwal::class.java)
                    if (data != null){
                        binding.startTV.setText(data.start+"-"+data.end)
                        binding.tanggalap.text = data.tanggal
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }

    override fun onBackPressed() {
        val intent = Intent(this, ListDokterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("type", 2)
        intent.putExtra("tanggal", binding.tanggalap.text)
        intent.putExtra("daerah", binding.workexpTV.text)
        startActivity(intent)
        finish()
    }

    private fun showdetailData(id : String) {

        val reference = FirebaseDatabase.getInstance().getReference("drh")
        reference.child(id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: drh? = snapshot.getValue(drh::class.java)
                    if (user != null) {
                        binding.namedrhTV.setText(user!!.Name)
                        binding.workexpTV.text = user!!.alamat
                        binding.tempatklinik.text = user!!.alamat
                        binding.tempattv.text = user!!.tempat
                        if (user!!.photoProfile == ""){
                            binding.photodrh.setImageResource(R.drawable.profile)
                        }else{
                            Glide.with(this@BookingActivity).load(user!!.photoProfile).into(binding.photodrh)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }

    private fun myPets(){
        val ref = FirebaseDatabase.getInstance().getReference("peliharaan")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pets)
        ref.orderByChild("pemilik")
            .equalTo(mAuth.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snapshot in snapshot.children) {
                        val item = snapshot.getValue(peliharaan::class.java)
                        pets.add(item?.nama.toString())
                        idpets.add(item?.id.toString())
                    }
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.mypetS.adapter = adapter
                    binding.mypetS.onItemSelectedListener = this@BookingActivity
                }
                    override fun onCancelled(error: DatabaseError) {
                        throw error.toException()
                    }

                })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        text= parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}

