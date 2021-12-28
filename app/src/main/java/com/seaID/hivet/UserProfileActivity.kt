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
import com.seaID.hivet.adapters.drhBookingAdapter
import com.seaID.hivet.models.drh


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

        mybinding.suntingTV.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("type", 2)
            startActivity(intent)
            finish()
        }

        mybinding.addpetsB.setOnClickListener {
            startActivity(Intent(this, PeliharaanActivity::class.java))
        }

        mybinding.gpasswordTV.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        counter++
        if (counter == 1){
            super.onBackPressed()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun dataPeliharaan() {
        mDbRef = FirebaseFirestore.getInstance()
        mDbRef.collection("peliharaan")
            .get()
            .addOnSuccessListener {
                val data = it.toObjects(peliharaan::class.java)
                val items = data.size
                if (items > 0){
                    for (item in data){
                        if (item.pemilik.equals(mAuth.currentUser!!.uid)){
                            petArrayList.add(item)
                        }
                    }
                    recyclerView.adapter = peliharaanAdapter
                    peliharaanAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun showChangePasswordDialog(view : View){
        val dialogBuilder = AlertDialog.Builder(this)
        val dialog = Dialog(this)
        //dialog.setContentView(R.layout.updat)
    }

private fun loadProfile(id : String) {
        val uidRef  = mDbRef.collection("users").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                mybinding.namauTV.text = user!!.name
                mybinding.emailTV.text = user!!.email
                if (user!!.photoProfile == "" || user.photoProfile == null){
                    mybinding.profileIM.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(mybinding.profileIM)
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