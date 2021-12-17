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
import com.seaID.hivet.adapters.drhAdapter
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.adapters.peliharaanAdapter
import com.seaID.hivet.models.User
import com.seaID.hivet.databinding.ActivityUserProfileBinding
import com.seaID.hivet.models.drh
import com.seaID.hivet.models.peliharaan
import android.R
import android.app.Dialog
import android.view.View


private lateinit var recyclerView: RecyclerView
private lateinit var petArrayList: ArrayList<peliharaan>
private lateinit var peliharaanAdapter: peliharaanAdapter


class UserProfileActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityUserProfileBinding

    //firebase auth
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: FirebaseFirestore

    //constant
    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //recyclerView = findViewById(R.id.pelihahaarnRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        petArrayList = arrayListOf()

        peliharaanAdapter = peliharaanAdapter(petArrayList)

        //init firebasee auth
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        val currentUser = mAuth.currentUser

        loadProfile(currentUser?.uid!!)

        dataPeliharaan()

        /*binding.suntingTV.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("Uid", mAuth.currentUser!!.uid)
            startActivity(intent)
            finish()
        }*/

    }

    private fun dataPeliharaan() {
        mDbRef = FirebaseFirestore.getInstance()
        mDbRef.collection("peliharaan")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ){
                    if (error != null){
                        Log.e("Error: ", error.message.toString())
                        return
                    }

                    for (dc : DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            petArrayList.add(dc.document.toObject(peliharaan::class.java))
                        }

                    }

                    recyclerView.adapter = peliharaanAdapter
                    peliharaanAdapter.notifyDataSetChanged()

                    }
            })
    }

    /* private fun showChangePasswordDialog(view : View){
        val dialogBuilder = AlertDialog.Builder(this)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.updat)
    } */

private fun loadProfile(id : String) {
        val uidRef  = mDbRef.collection("users").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                binding.namauTV.text = user!!.name
                binding.emailTV.text = user!!.email
                if (user!!.photoProfile == ""){
                    //binding.profileIM.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(binding.profileIM)
                }
                Log.d(TAG, "{$user.name}")
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

    }


}