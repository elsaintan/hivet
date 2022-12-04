package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.models.User
import com.seaID.hivet.databinding.ActivityUserProfileBinding
import com.seaID.hivet.models.peliharaan
import android.app.Dialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.models.drh
import java.util.*


class UserProfileActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var petArrayList: ArrayList<peliharaan>
    private lateinit var peliharaanAdapter: peliharaanAdapter

    private lateinit var mybinding : ActivityUserProfileBinding

    //firebase auth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: FirebaseFirestore

    var counter : Int = 0

    //constant
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mybinding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = mybinding.root
        setContentView(view)

        recyclerView = mybinding.peliharaanRV
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        petArrayList = arrayListOf()

        peliharaanAdapter = peliharaanAdapter(petArrayList)

        //init firebasee auth
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        loadProfile(mAuth.currentUser!!.uid)

        dataPeliharaan()

        //Toast.makeText(this, photo, Toast.LENGTH_SHORT).show()


        mybinding.suntingTV.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("type", 2)
            startActivity(intent)
        }

        mybinding.addpetsB.setOnClickListener {
            startActivity(Intent(this, PeliharaanActivity::class.java))
        }

        mybinding.gpasswordTV.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }


        //Toast.makeText(this, " "+ mybinding.profileIM.drawable , Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }

    private fun dataPeliharaan() {

        val ref = FirebaseDatabase.getInstance().getReference("peliharaan")
        ref.orderByChild("pemilik")
            .equalTo(mAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    petArrayList.clear()
                    for (snapshot in snapshot.children) {
                        val item : peliharaan? = snapshot.getValue(peliharaan::class.java)
                        petArrayList.add(item!!)
                    }

                    recyclerView.adapter = peliharaanAdapter
                    peliharaanAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
    }

    private fun showChangePasswordDialog(view : View){
        val dialogBuilder = AlertDialog.Builder(this)
        val dialog = Dialog(this)
        //dialog.setContentView(R.layout.updat)
    }

private fun loadProfile(id : String) {
    val reference = FirebaseDatabase.getInstance().getReference()
    reference.child("users").child(id)
        .addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val data : User? = snapshot.getValue(User::class.java)
                if (data != null) {
                    mybinding.namauTV.text = data.name
                    mybinding.emailTV.text = data.email

                    if (data.photoProfile == "" || data.photoProfile == null){
                        mybinding.profileIM.setImageResource(R.drawable.profile)

                    }else{
                        Glide.with(this@UserProfileActivity).load(data.photoProfile).into(mybinding.profileIM)
                    }
                }else{
                    Toast.makeText(this@UserProfileActivity, "No such document", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        })

    }


}