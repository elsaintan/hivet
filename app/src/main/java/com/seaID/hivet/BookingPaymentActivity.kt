package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
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
import com.seaID.hivet.databinding.ActivityBookingPaymentBinding
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.jadwal
import com.seaID.hivet.models.peliharaan
import java.text.SimpleDateFormat
import java.util.*

class BookingPaymentActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var bbinding: ActivityBookingPaymentBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bbinding = ActivityBookingPaymentBinding.inflate(layoutInflater)
        val view = bbinding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()
        initMidtransSdk()

        bbinding.namedrhTV.text = intent.getStringExtra("name")
        //bbinding.tanggalap.text = intent.getStringExtra("name")

        bbinding.gopayBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.GO_PAY)
        }
        bbinding.spayBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.SHOPEEPAY)
        }
        bbinding.akulakuBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.AKULAKU)
        }

    }

    private fun initMidtransSdk() {
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            .setClientKey(Constant.CLIENT_KEY_MIDTRANS) // client_key is mandatory
            .setContext(this) // context is mandatory
            .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
            .setMerchantBaseUrl(Constant.BASE_URL_MIDTRANS) //set merchant url
            .setUIkitCustomSetting(uiKitCustomSetting())
            .enableLog(true) // enable sdk log
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")) // will replace theme on snap theme on MAP
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS ->
                    //Toast.makeText(this, "Transaction Finished. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                    saveAppointment(result.response.transactionId)
                TransactionResult.STATUS_PENDING ->
                    Toast.makeText(this, "Transaction Pending. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                TransactionResult.STATUS_FAILED ->
                    Toast.makeText(this, "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage, Toast.LENGTH_LONG).show()
            }
            result.response.validationMessages
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun waktu(){
        var waktu = ""
        var duration = 0
        var slot = 0
        val id_drh = intent.getStringExtra("drh")

        val reference = FirebaseDatabase.getInstance().getReference()
        reference.child("janjiTemu").child(id_drh.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data= snapshot.getValue(jadwal::class.java)
                    if (data != null){
                        waktu = data.start.toString()
                        duration = data.duration!!.toInt()
                        slot = data.slot!!.toInt()
                        setTime(waktu, duration, slot)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
    }

    private fun setTime(waktu: String, duration: Int, slot: Int){
        val df = SimpleDateFormat("HH:mm")
        val d: Date = df.parse(waktu)
        val cal: Calendar = Calendar.getInstance()
        cal.setTime(d)
        cal.add(Calendar.MINUTE, duration)
        val newTime = df.format(cal.getTime())
        bbinding.newTime.setText(newTime)
        val slot = slot - 1

        updateJadwal(newTime,slot)

    }

    private fun updateJadwal(newTime: String, slot: Int) {
        val id = intent.getStringExtra("drh")
        val reference = FirebaseDatabase.getInstance().getReference("janjiTemu")
        reference!!.child(id.toString())
            .get()
            .addOnSuccessListener {
                val hm = HashMap<String, Any>()
                hm["start"] = newTime
                hm["slot"] = slot
                reference.updateChildren(hm)
            }
            .addOnFailureListener {
                throw it
            }
    }

    private fun saveAppointment(transaction_id : String) {

        waktu()

        val length = 8
        val id : String = getRandomString(length)
        val time = bbinding.newTime.text

        val booking = booking(id, transaction_id, mAuth.currentUser!!.uid, intent.getStringExtra("pet"),
            intent.getStringExtra("drh"), time.toString(), intent.getStringExtra("tanggal"),"Berhasil Reservasi")

        mDbRef.collection("booking_appointments").document(id).set(booking)
            .addOnCompleteListener {
                Toast.makeText(this, "OKE", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BookingBerhasilActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("kode_booking", id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                //stored data failed
                Toast.makeText(this, "Action failed due to " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun uiKitCustomSetting(): UIKitCustomSetting {
        val uIKitCustomSetting = UIKitCustomSetting()
        uIKitCustomSetting.isSkipCustomerDetailsPages = true
        uIKitCustomSetting.isShowPaymentStatus = true
        return uIKitCustomSetting
    }

    private fun initTransactionRequest(): TransactionRequest {
        // Create new Transaction Request
        val transactionRequestNew = TransactionRequest(System.currentTimeMillis().toString() + "", 5000.0)
        transactionRequestNew.customerDetails = initCustomerDetails()
        transactionRequestNew.gopay = Gopay("mysamplesdk:://midtrans")
        transactionRequestNew.shopeepay = Shopeepay("mysamplesdk:://midtrans")
        return transactionRequestNew
    }

    private fun initCustomerDetails(): CustomerDetails {
        //define customer detail (mandatory for coreflow)
        val mCustomerDetails = CustomerDetails()
        mCustomerDetails.firstName = mAuth.currentUser!!.displayName
        mCustomerDetails.email = mAuth.currentUser!!.email
        mCustomerDetails.customerIdentifier = mAuth.currentUser!!.email
        return mCustomerDetails
    }
}