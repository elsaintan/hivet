package com.seaID.hivet.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.seaID.hivet.R
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.ChatActivity
import com.seaID.hivet.KonsulPaymentActivity
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.konsultasi
import com.seaID.hivet.models.peliharaan
import java.util.*

class KonsultasiAdapter(private val konsultasiList : ArrayList<konsultasi>) : RecyclerView.Adapter<KonsultasiAdapter.MyViewHolder>(){

    private lateinit var mDbRef: FirebaseFirestore
    var reference: DatabaseReference? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(com.seaID.hivet.R.layout.user_dokter_layout, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: KonsultasiAdapter.MyViewHolder, position: Int) {
        val konsultasi : konsultasi = konsultasiList[position]

        val ref = FirebaseDatabase.getInstance().getReference("drh").child(konsultasi.id_drh.toString())
            .addValueEventListener(object : ValueEventListener{
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

        val peliharaan = FirebaseDatabase.getInstance().getReference("peliharaan").child(konsultasi.id_pet.toString())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pet: peliharaan? = snapshot.getValue(peliharaan::class.java)
                    if (pet != null){
                        holder.tanggal.text = pet.jenis
                        holder.namah.text = pet.nama
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }

            })


        when(konsultasi.status){
            "1" -> holder.button.text = "Menunggu"
            "2" -> holder.button.text = "Bayar"
            "3" -> holder.button.text = "Chat"
            "4" -> holder.button.text = "Selesai"
            "5" -> holder.button.text = "Ditolak"
            "6" -> holder.button.text = "Kadaluwarsa"
        }

        holder.button.setOnClickListener {
            if (holder.button.text == "Chat"){
                val intent = Intent(holder.itemView.context, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("Uid", konsultasi.id_drh)
                intent.putExtra("id", konsultasi.id)
                intent.putExtra("idpet", konsultasi.id_pet)
                intent.putExtra("tanggal", konsultasi.tanggal)
                intent.putExtra("idtransaction", konsultasi.id_transaction)
                intent.putExtra("harga", konsultasi.harga.toString())
                holder.itemView.context.startActivity(intent)
            }else if(holder.button.text == "Selesai"){
                val intent = Intent(holder.itemView.context, ChatActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("Uid", konsultasi.id_drh)
                intent.putExtra("type", "0")
                intent.putExtra("id", konsultasi.id)
                intent.putExtra("idpet", konsultasi.id_pet)
                intent.putExtra("tanggal", konsultasi.tanggal)
                holder.itemView.context.startActivity(intent)
            }else if (holder.button.text == "Bayar"){
                val namaa = holder.nama.text
                val intent = Intent(holder.itemView.context, KonsulPaymentActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("Uid", konsultasi.id_drh)
                intent.putExtra("id", konsultasi.id)
                intent.putExtra("id_pet", konsultasi.id_pet)
                intent.putExtra("tanggal", konsultasi.tanggal)
                intent.putExtra("harga", konsultasi.harga)
                intent.putExtra("type", "2")
                intent.putExtra("nama_drh", namaa)
                holder.itemView.context.startActivity(intent)
            }

        }
    }


    override fun getItemCount(): Int {
        return konsultasiList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val nama : TextView = itemView.findViewById(com.seaID.hivet.R.id.namedrhTV)
        val namah : TextView = itemView.findViewById(com.seaID.hivet.R.id.workexpTV)
        val tanggal : TextView = itemView.findViewById(com.seaID.hivet.R.id.priceTV)
        val foto : ImageView = itemView.findViewById(com.seaID.hivet.R.id.photodrh)
        val button : Button = itemView.findViewById(com.seaID.hivet.R.id.konsulbt)

        override fun onClick(v: View?) {

        }

    }
}