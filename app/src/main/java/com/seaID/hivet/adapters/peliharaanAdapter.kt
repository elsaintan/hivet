package com.seaID.hivet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.seaID.hivet.KonsultasiActivity
import com.seaID.hivet.PeliharaanActivity
import com.seaID.hivet.R
import com.seaID.hivet.models.drh
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
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PeliharaanActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("id", pet.pemilik)
            intent.putExtra("nama", pet.nama)
            intent.putExtra("jenis", pet.jenis)
            intent.putExtra("ket", pet.keterangan)
            intent.putExtra("type", 2)
            holder.itemView.context.startActivity(intent)
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