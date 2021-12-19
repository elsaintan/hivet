package com.seaID.hivet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seaID.hivet.BookingActivity
import com.seaID.hivet.KonsultasiActivity
import com.seaID.hivet.R
import com.seaID.hivet.models.drh


class drhBookingAdapter(private val drhList : ArrayList<drh>) : RecyclerView.Adapter<drhBookingAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_dokter_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drh : drh = drhList[position]

            if (drh!!.photoProfile == ""){
                holder.profileIM.setImageResource(R.drawable.profile)
            }else{
                Glide.with(holder.itemView.context).load(drh!!.photoProfile).into(holder.profileIM)
            }

            holder.name.text = drh.Name
            holder.workexp.text = drh.tempat
            holder.price.text = drh.alamat
            holder.button.setText("Reservasi")

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, BookingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("Uid", drh.id)
            intent.putExtra("Name", drh.Name)
            intent.putExtra("ProfilePic", drh.photoProfile)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return drhList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val name : TextView = itemView.findViewById(R.id.namedrhTV)
        val workexp : TextView = itemView.findViewById(R.id.workexpTV)
        val price : TextView = itemView.findViewById(R.id.priceTV)
        val button : Button = itemView.findViewById(R.id.konsulbt)
        val profileIM : ImageView = itemView.findViewById(R.id.photodrh)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v : View?){

        }
    }


}