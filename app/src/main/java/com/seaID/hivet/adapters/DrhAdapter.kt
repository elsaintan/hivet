package com.seaID.hivet.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.seaID.hivet.KonsultasiActivity
import com.seaID.hivet.R
import com.seaID.hivet.RetrofitIntance
import com.seaID.hivet.models.PushNotifKonsul
import com.seaID.hivet.models.drh
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var topic = ""

class drhAdapter(private val drhList : ArrayList<drh>) : RecyclerView.Adapter<drhAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_dokter_layout, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val drh : drh = drhList[position]
            holder.name.text = drh.Name
            holder.workexp.text = drh.WorkExp + " Tahun"
            holder.price.text = "Rp. "+drh.harga

        if (drh!!.photoProfile == ""){
            holder.profileIM.setImageResource(R.drawable.profile)
        }else{
            Glide.with(holder.itemView.context).load(drh!!.photoProfile).into(holder.profileIM)
        }


        /** holder.button.setOnClickListener{
            topic = "topic/${drh.id}"
            PushNotifKonsul(NotificationKonsulData(drh!!.Name!!, "Anda mendapatkan satu permintaan konsultasi baru"),
            topic).also {
                sendNotifKonsul(it)
            }
        } **/

        holder.button.setOnClickListener {
            val intent = Intent(holder.itemView.context, KonsultasiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("Uid", drh.id)
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
        val profileIM : ImageView = itemView.findViewById(R.id.photodrh)
        val button : Button = itemView.findViewById(R.id.konsulbt)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v : View?){

        }
    }


    private fun sendNotifKonsul(notification: PushNotifKonsul) = CoroutineScope(Dispatchers.IO).launch{
        try {
            val response = RetrofitIntance.api.pushNotifKonsul(notification)
            if (response.isSuccessful){
                Log.e("Error", "Response ${Gson().toJson(response)}")
            }else{
                Log.e("Error", response.errorBody().toString())
            }
        }catch (e: Exception){
            Log.d("Error","Error "+e.message)
        }
    }

}
