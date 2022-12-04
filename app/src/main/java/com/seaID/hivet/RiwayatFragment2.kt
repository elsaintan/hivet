package com.seaID.hivet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.JanjiTemuAdapter
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.peliharaan
import java.util.*


class RiwayatFragment2 : Fragment() {

    private lateinit var janjiTemuList: ArrayList<booking>
    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.riwayat_fragment2, container, false)

        val mRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        janjiTemuList = arrayListOf()
        val adapter : JanjiTemuAdapter = JanjiTemuAdapter(janjiTemuList)

        val db = FirebaseDatabase.getInstance().getReference("booking_appointments")
        mAuth = FirebaseAuth.getInstance()

        db.orderByChild("user_id")
            .equalTo(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snapshot in snapshot.children) {
                        val item : booking? = snapshot.getValue(booking::class.java)
                        janjiTemuList.add(item!!)
                    }
                    mRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })

        return fragmentView
    }

}