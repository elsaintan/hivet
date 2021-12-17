package com.seaID.hivet.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.seaID.hivet.R
import com.seaID.hivet.models.peliharaan

private lateinit var mAuth: FirebaseAuth

class peliharaanAdapter(private val petList : ArrayList<peliharaan>) : RecyclerView.Adapter<peliharaanAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.peliharaan_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: peliharaanAdapter.MyViewHolder, position: Int) {
        mAuth = FirebaseAuth.getInstance()
        val pet : peliharaan = petList[position]
        if (pet.pemilik == mAuth.currentUser!!.uid){
            holder.name.text = pet.nama
            holder.jenis.text = pet.jenis
        }

    }

    override fun getItemCount(): Int {
        return petList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val name : TextView = itemView.findViewById(R.id.namaTV)
        val jenis : TextView = itemView.findViewById(R.id.jenisTV)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v : View?){

        }
    }
}