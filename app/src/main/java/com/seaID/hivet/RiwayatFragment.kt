package com.seaID.hivet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.JanjiTemuAdapter
import com.seaID.hivet.adapters.KonsultasiAdapter
import com.seaID.hivet.models.booking
import com.seaID.hivet.models.konsultasi


class RiwayatFragment : Fragment() {

    private lateinit var konsultasiList: ArrayList<konsultasi>
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var  mRecyclerView : RecyclerView
    private lateinit var adapter: KonsultasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.riwayat_fragment, container, false)

        mRecyclerView = fragmentView.findViewById(R.id.recyclerView)
        val search = fragmentView.findViewById<EditText>(R.id.search)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        konsultasiList = arrayListOf()
        adapter = KonsultasiAdapter(konsultasiList)

        search.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
           
            override fun afterTextChanged(s: Editable?) {
                val searchText = search.getText().toString().trim()

                loadFirebaseData(searchText)
            }

        })

        mAuth = FirebaseAuth.getInstance()
        val reference = FirebaseDatabase.getInstance().getReference("konsultasi").orderByKey().limitToLast(100)

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
                throw error.toException()
            }
        })



        return fragmentView
    }

    private fun loadFirebaseData(searchText: String) {
        mAuth = FirebaseAuth.getInstance()

        if(searchText.isEmpty()){
            val reference = FirebaseDatabase.getInstance().getReference("konsultasi").orderByKey().limitToLast(100)

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
                    throw error.toException()
                }
            })
        }else {

            val reference = FirebaseDatabase.getInstance().getReference("konsultasi").orderByChild("tanggal").startAt(searchText)

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
                    throw error.toException()
                }
            })
        }

    }

}