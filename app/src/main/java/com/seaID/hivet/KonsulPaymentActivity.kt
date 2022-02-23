package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
import com.seaID.hivet.models.konsultasi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class KonsulPaymentActivity : AppCompatActivity(), TransactionFinishedCallback {

    private lateinit var kbinding : ActivityKonsulPaymentBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kbinding = ActivityKonsulPaymentBinding.inflate(layoutInflater)
        val view = kbinding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        kbinding.namedrhTV.text = intent.getStringExtra("nama_drh")
        kbinding.tanggalap.text = intent.getStringExtra("harga")
        kbinding.hargatot.text = intent.getStringExtra("harga")


        initMidtransSdk()

        kbinding.gopayBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.GO_PAY)
        }
        kbinding.spayBt.setOnClickListener {
            MidtransSDK.getInstance().transactionRequest = initTransactionRequest()
            MidtransSDK.getInstance().startPaymentUiFlow(this, PaymentMethod.SHOPEEPAY)
        }
        kbinding.akulakuBt.setOnClickListener {
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

    private fun saveKonusl(transactionId: String?){
        val id = intent.getStringExtra("id")
        val id_drh = intent.getStringExtra("Uid")
        val id_pet = intent.getStringExtra("id_pet")
        val tanggal = intent.getStringExtra("tanggal")
        val konsultasi = konsultasi(id, id_drh, mAuth.uid, id_pet,tanggal,"Telah Bayar")
        mDbRef.collection("konsultasi").document(id.toString()).set(konsultasi)
            .addOnSuccessListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("Uid", id_drh)
                startActivity(intent)
            }.addOnFailureListener {

            }
    }

    private fun uiKitCustomSetting(): UIKitCustomSetting {
        val uIKitCustomSetting = UIKitCustomSetting()
        uIKitCustomSetting.isSkipCustomerDetailsPages = true
        uIKitCustomSetting.isShowPaymentStatus = true
        return uIKitCustomSetting
    }

    private fun initTransactionRequest(): TransactionRequest {
        // Create new Transaction Request
        val transactionRequestNew = TransactionRequest(System.currentTimeMillis().toString() + "", intent.getStringExtra("harga")!!.toDouble())
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