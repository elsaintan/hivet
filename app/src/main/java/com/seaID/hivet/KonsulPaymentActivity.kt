package com.seaID.hivet

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.EventListener
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
import com.seaID.hivet.databinding.ActivityBookingPaymentBinding
import com.seaID.hivet.databinding.ActivityKonsulPaymentBinding
import com.seaID.hivet.models.Chat
import com.seaID.hivet.models.Saldo
import com.seaID.hivet.models.konsultasi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.HashMap

class KonsulPaymentActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var kbinding : ActivityKonsulPaymentBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kbinding = ActivityKonsulPaymentBinding.inflate(layoutInflater)
        val view = kbinding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        retriveData()


        initMidtransSdk()

        kbinding.gopayBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.GO_PAY)
        }
        kbinding.spayBt.setOnClickListener {
            //Toast.makeText(this, kbinding.hargatot.text, Toast.LENGTH_SHORT).show()
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.SHOPEEPAY)
        }



    }

    override fun onBackPressed() {
        val intent = Intent(this, ListDokterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("type", 1)

        startActivity(intent)
        finish()
    }

    private fun retriveData() {
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("namedrh")
        kbinding.namedrhTV.setText(name)
        reference = FirebaseDatabase.getInstance().getReference("konsultasi").child(id.toString())
        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val konsul: konsultasi? = snapshot.getValue(konsultasi::class.java)
                kbinding.tanggalap.text = konsul!!.harga.toString()
                kbinding.hargatot.text = konsul.harga.toString()
            }


            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        })



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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS ->
                    //Toast.makeText(this, "Transaction Finished. ID: " + result.response.transactionId, Toast.LENGTH_LONG).show()
                    saveKonusl(result.response.transactionId)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveKonusl(transactionId: String?){
        val id = intent.getStringExtra("id")
        val id_drh = intent.getStringExtra("Uid")

        reference = FirebaseDatabase.getInstance().getReference("konsultasi")
        val hm = HashMap<String, Any>()
        hm["id_transaction"] = transactionId.toString()
        hm["status"] = "3"


        reference!!.child(id.toString()).updateChildren(hm)
            .addOnSuccessListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("Uid", id_drh)
                intent.putExtra("id", id)
                startActivity(intent)
            }
            .addOnFailureListener {
                throw it
            }

        saveTransaction(transactionId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTransaction(transactionId: String?) {
        val length = 8
        val id : String = getRandomString(length)
        val id_drh = intent.getStringExtra("Uid")
        val current = LocalDateTime.now()
        val simpleDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val tanggal = current.format(simpleDateFormat)
        val tariksaldo = Saldo(id, id_drh, kbinding.hargatot.text.toString(), "", "", "", tanggal, "Berhasil", "Pemasukan")
        mDbRef.collection("saldo").document(id).set(tariksaldo)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                Toast.makeText(this, "Error "+it.message, Toast.LENGTH_SHORT).show()
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
        val harga = kbinding.hargatot.text.toString()
        val transactionRequestNew = TransactionRequest(System.currentTimeMillis().toString() + "", harga!!.toDouble())
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