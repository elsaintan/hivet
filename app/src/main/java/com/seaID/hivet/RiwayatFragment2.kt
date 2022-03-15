package com.seaID.hivet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.JanjiTemuAdapter
import com.seaID.hivet.models.booking


class RiwayatFragment2 : Fragment() {

    private lateinit var janjiTemuList: ArrayList<booking>
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
        val fragmentView = inflater.inflate(R.layout.riwayat_fragment2, container, false)

        val mRecyclerView = fragmentView.findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        janjiTemuList = arrayListOf()
        val adapter : JanjiTemuAdapter = JanjiTemuAdapter(janjiTemuList)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        db.collection("booking_appointments")
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(booking::class.java)
                val items = data.size
                if (items > 0){
                    for (item in data){
                        if(item.user_id == mAuth.currentUser!!.uid){
                            janjiTemuList.add(item)
                        }
                    }
                    mRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

        return fragmentView
    }

}