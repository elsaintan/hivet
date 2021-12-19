package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.models.drh
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

    private lateinit var coba : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_dokter)

        val type = intent.getIntExtra("type", 0)

        //coba = findViewById(R.id.cek)
        //coba.text = type.toString()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        drhArrayList = arrayListOf()

        drhAdapter = drhAdapter(drhArrayList)
        drhBookingAdapter = drhBookingAdapter(drhArrayList)

        if (type == 1){
            EventChangeListener()
        }else if (type == 2){
            doctorRetrieveData()
        }


    }

    private fun EventChangeListener() {
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

                    var listdrh = ArrayList<drh>()
                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            val vet: drh = dc.document.toObject(drh::class.java)
                            drhArrayList.add(dc.document.toObject(drh::class.java))
                        }
                    }
                    recyclerView.adapter = drhAdapter
                    drhAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun doctorRetrieveData() {
        db = FirebaseFirestore.getInstance()
            db.collection("drh")
                .get()
                .addOnSuccessListener {
                    val data = it.toObjects(drh::class.java)
                    val items = data.size
                    if (items > 0){
                        for (item in data){
                            if (item.booking == "1"){
                                drhArrayList.add(item)
                            }
                        }
                        recyclerView.adapter = drhBookingAdapter
                        drhBookingAdapter.notifyDataSetChanged()
                    }
                }
    }
}