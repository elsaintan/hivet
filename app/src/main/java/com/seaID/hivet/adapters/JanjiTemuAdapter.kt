package com.seaID.hivet.adapters

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.R
import com.seaID.hivet.RincianJanjiTemuActivity
import com.seaID.hivet.models.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class JanjiTemuAdapter(private val janjitemuList: ArrayList<booking>) : RecyclerView.Adapter<JanjiTemuAdapter.MyViewHolder>(){

    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_dokter_layout, parent, false)
        return MyViewHolder(itemView)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data : booking = janjitemuList[position]

        holder.tanggal.text = data.tanggal

        val dateInString = data.tanggal

        if (isDateValid(dateInString.toString())){
            holder.button.text = "Selesai"
            //Toast.makeText(holder.itemView.context, "This is true", Toast.LENGTH_SHORT).show()
        }else{
            holder.button.text = "Rincian"
            //Toast.makeText(holder.itemView.context, "This is false", Toast.LENGTH_SHORT).show()
        }

        val ref = FirebaseDatabase.getInstance().getReference("drh").child(data.drh_id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: drh? = snapshot.getValue(drh::class.java)
                    if (user != null){
                        holder.nama.text = user.Name
                        if (user.photoProfile != null){
                            holder.foto.setImageResource(R.drawable.profildrh)
                        }else{
                            Glide.with(holder.itemView.context).load(user.photoProfile).into(holder.foto)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })

        val peliharaan = FirebaseDatabase.getInstance().getReference("peliharaan").child(data.pet_id.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pet: peliharaan? = snapshot.getValue(peliharaan::class.java)
                    if (pet != null){
                        holder.namah.text = pet.jenis
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })

        holder.button.setOnClickListener {
            val intent = Intent(holder.itemView.context, RincianJanjiTemuActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("id", data.kode_booking)
            holder.itemView.context.startActivity(intent)
        }

    }

    fun isDateValid(myDate: String) : Boolean {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd").parse(myDate)
            return !date.before(Date())
        } catch(ignored: java.text.ParseException) {
            return false
        }
    }

    override fun getItemCount(): Int {
        return janjitemuList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val nama : TextView = itemView.findViewById(R.id.namedrhTV)
        val namah : TextView = itemView.findViewById(R.id.workexpTV)
        val tanggal : TextView = itemView.findViewById(R.id.priceTV)
        val foto : ImageView = itemView.findViewById(R.id.photodrh)
        val button : Button = itemView.findViewById(R.id.konsulbt)

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }
}


