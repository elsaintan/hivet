package com.seaID.hivet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class drhAdapter(private val drhList : ArrayList<drh>) : RecyclerView.Adapter<drhAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): drhAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_dokter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: drhAdapter.MyViewHolder, position: Int) {
        val drh : drh = drhList[position]
        holder.name.text = drh.Name
        holder.workexp.text = drh.WorkExp + " Tahun"
        holder.price.text = "Rp. 35000"
    }

    override fun getItemCount(): Int {
        return drhList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.namedrhTV)
        val workexp : TextView = itemView.findViewById(R.id.workexpTV)
        val price : TextView = itemView.findViewById(R.id.priceTV)
    }
}