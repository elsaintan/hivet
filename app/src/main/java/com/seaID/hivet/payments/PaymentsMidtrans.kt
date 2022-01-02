package com.seaID.hivet.payments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.seaID.hivet.Constant.CLIENT_KEY_MIDTRANS
import com.seaID.hivet.Constant.BASE_URL_MIDTRANS
import com.google.android.gms.common.internal.service.Common
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import com.seaID.hivet.R
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback



class PaymentsMidtrans : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments_midtrans)

        SdkUIFlowBuilder.init().setClientKey(CLIENT_KEY_MIDTRANS).setContext(applicationContext)
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
                }).setMerchantBaseUrl(BASE_URL_MIDTRANS).enableLog(true).setColorTheme(
                CustomColorTheme("#FFE51255", "#B61548", "#FFE51255")
            ).setLanguage("id").buildSDK()
    }
}