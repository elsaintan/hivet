package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.konsultasi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class ListDokterActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var drhArrayList: ArrayList<drh>
    private lateinit var drhAdapter: drhAdapter
    private lateinit var drhBookingAdapter: drhBookingAdapter
    private lateinit var db : FirebaseFirestore

    var tanggal : String ?= null
    var daerah : String ?= null
    var counter : Int = 0
    var type : Int ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_dokter)

        type = intent.getIntExtra("type", 0)
        tanggal = intent.getStringExtra("tanggal")
        daerah = intent.getStringExtra("daerah")

        //Toast.makeText(this, daerah, Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "Date "+tanggal, Toast.LENGTH_SHORT).show()

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

    override fun onBackPressed() {
        //super.onBackPressed()
        counter++
        if (counter == 1){
            if(type == 1){
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else if (type == 2){
                val intent = Intent(this, FilterBookingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("type", 2)
                intent.putExtra("tanggal", tanggal)
                intent.putExtra("daerah", daerah)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun EventChangeListener() {
        val reference = FirebaseDatabase.getInstance().getReference("drh").orderByKey().limitToLast(100)
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                drhArrayList.clear()
                for (snapshot in snapshot.children) {
                    val data : drh? = snapshot.getValue(drh::class.java)
                    drhArrayList.add(data!!)
                    recyclerView.adapter = drhAdapter
                }
                drhAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }

        })
    }

    private fun doctorRetrieveData() {
        val tgl = intent.getStringExtra("tanggal")
        val dom = intent.getStringExtra("daerah")
        val reference = FirebaseDatabase.getInstance().getReference("drh")
        reference.orderByChild("booking")
            .equalTo(dom+"_"+tgl)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    drhArrayList.clear()
                    for (snapshot in snapshot.children) {
                        val data: drh? = snapshot.getValue(drh::class.java)
                        if (data?.status == "1") {
                            drhArrayList.add(data!!)
                        }
                        recyclerView.adapter = drhBookingAdapter
                    }
                    drhBookingAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })
        //Toast.makeText(this, dom+"_"+tgl, Toast.LENGTH_SHORT).show()
    }
}