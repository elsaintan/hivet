package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList

class ListDokterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var drhArrayList: ArrayList<drh>
    private lateinit var drhAdapter: drhAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_dokter)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        drhArrayList = arrayListOf()

        drhAdapter = drhAdapter(drhArrayList)

        recyclerView.adapter = drhAdapter

        EventChangeListener()


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

                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            drhArrayList.add(dc.document.toObject(drh::class.java))
                        }
                    }
                    drhAdapter.notifyDataSetChanged()
                }
            })
    }
}