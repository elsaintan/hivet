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
import com.seaID.hivet.adapters.KonsultasiAdapter
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.konsultasi


class RiwayatFragment : Fragment() {

    private lateinit var konsultasiList: ArrayList<konsultasi>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.riwayat_fragment, container, false)

        val mRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        konsultasiList = arrayListOf()
        val adapter: KonsultasiAdapter = KonsultasiAdapter(konsultasiList)

        mAuth = FirebaseAuth.getInstance()

        val reference = FirebaseDatabase.getInstance().getReference("konsultasi")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                konsultasiList.clear()
                for (snapshot in snapshot.children) {
                    val data: konsultasi? = snapshot.getValue(konsultasi::class.java)
                    if (data?.id_user == mAuth.currentUser?.uid) {
                        konsultasiList.add(data!!)
                    }
                    mRecyclerView.adapter = adapter
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException();
            }
        })

        return fragmentView
    }

}