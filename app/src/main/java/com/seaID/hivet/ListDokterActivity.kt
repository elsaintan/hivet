package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.gson.Gson
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.models.PushNotifKonsul
import com.seaID.hivet.models.drh
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ListDokterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var drhArrayList: ArrayList<drh>
    private lateinit var drhAdapter: drhAdapter
    private lateinit var drhBookingAdapter: drhBookingAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_dokter)

        val type = intent.getStringExtra("type")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        drhArrayList = arrayListOf()

        drhAdapter = drhAdapter(drhArrayList)
        drhBookingAdapter = drhBookingAdapter(drhArrayList)

        EventChangeListener(type!!.toInt())
    }

    private fun EventChangeListener(type : Int) {
        db = FirebaseFirestore.getInstance()
        db.collection("drh")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ){
                    if (error != null){
                        Log.e("Error: ", error.message.toString())
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            drhArrayList.add(dc.document.toObject(drh::class.java))
                        }

                    }
                    if (type == 1){
                        recyclerView.adapter = drhAdapter
                        drhAdapter.notifyDataSetChanged()
                    }else if (type == 0){
                        recyclerView.adapter = drhBookingAdapter
                        drhBookingAdapter.notifyDataSetChanged()
                    }

                }
            })
    }
}