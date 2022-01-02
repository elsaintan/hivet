package com.seaID.hivet

import android.Manifest
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
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
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
    var counter : Int = 0
    private var transactionResult = TransactionResult()
    private var totalProduct: Int = 123456;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val uId = intent.getStringExtra("Uid")
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        myPets()
        showdetailData(uId.toString())

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), 101)
        }

        SdkUIFlowBuilder.init().setClientKey(Constant.CLIENT_KEY_MIDTRANS).setContext(applicationContext)
            .setTransactionFinishedCallback(
                TransactionFinishedCallback {
                    if (TransactionResult.STATUS_SUCCESS == "success") {
                        Toast.makeText(this, "Success transaction", Toast.LENGTH_LONG).show()
                    } else if (TransactionResult.STATUS_PENDING == "pending") {
                        Toast.makeText(this, "Pending transaction", Toast.LENGTH_LONG).show()
                    } else if (TransactionResult.STATUS_FAILED == "failed") {
                        Toast.makeText(
                            this,
                            "Failed ${it.response.statusMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (it.status.equals(
                            TransactionResult.STATUS_INVALID,
                            true
                        )
                    ) {
                        Toast.makeText(this, "Invalid transaction", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failure transaction", Toast.LENGTH_LONG).show()
                    }
                }).setMerchantBaseUrl(Constant.BASE_URL_MIDTRANS).enableLog(true).setColorTheme(
                CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")
            ).setLanguage("id").buildSDK()

        binding.reservasiBt.setOnClickListener {
            Toast.makeText(this, "Open transaction", Toast.LENGTH_LONG).show()


            val transactionRequest = TransactionRequest(
                "Payment-Midtrans" + System.currentTimeMillis().toString(), //transaction id
                totalProduct.toDouble() //jumlah total transaksi
            )
            val detail = com.midtrans.sdk.corekit.models.ItemDetails(
                "NamaItemId", //item id
                totalProduct.toDouble(), //harga item
                5, //jumlah item
                "Reservasi (Nama)" //nama item
            )
            val itemDetails = ArrayList<com.midtrans.sdk.corekit.models.ItemDetails>()

            itemDetails.add(detail)
            //uiKitDetails(transactionRequest)
            transactionRequest.itemDetails = itemDetails
            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)

            TransactionResult.STATUS_SUCCESS
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        counter++
        if (counter == 1){
            super.onBackPressed()
        }
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
                val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pets)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.mypetS.adapter = adapter
                binding.mypetS.onItemSelectedListener = this
            }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        text= parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}