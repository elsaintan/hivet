package com.seaID.hivet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Value
import com.seaID.hivet.models.User
import com.seaID.hivet.models.konsultasi
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore

    private lateinit var uName : TextView
    private lateinit var uPhoto : CircleImageView
    private lateinit var bLogout : ImageView
    private lateinit var bKonsul : ImageButton
    private lateinit var bBooking : ImageButton
    private lateinit var profile : ImageView
    private lateinit var konsultasi : ImageView
    private lateinit var home : ImageView
    private lateinit var artikel : Button

    private var backPressedTime = 0L

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance()

        val uId = mAuth.currentUser!!.uid

        setUser(uId)

        uName = findViewById(R.id.userName)
        uPhoto = findViewById(R.id.foto)
        bLogout = findViewById(R.id.imageLogout)
        bKonsul = findViewById(R.id.bkonsultasi)
        bBooking = findViewById(R.id.bBooking)
        profile = findViewById(R.id.imageSetting)
        konsultasi = findViewById(R.id.imageRiwayat)
        home = findViewById(R.id.imageHome)
        artikel = findViewById(R.id.tombol_selengkapnya)

        home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        bLogout.setOnClickListener {
            mAuth.signOut()
            //start login activity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        bKonsul.setOnClickListener {
            val intent = Intent(this, ListDokterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("type", 1)
            intent.putExtra("username", uName.getText().toString())

            startActivity(intent)
        }

        bBooking.setOnClickListener{
            startActivity(Intent(this, FilterBookingActivity::class.java))
            finish()
        }

        profile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
            finish()
        }

        konsultasi.setOnClickListener {
            startActivity(Intent(this, RiwayatLayoutActivity::class.java))
            finish()
        }

        artikel.setOnClickListener {
            startActivity(Intent(this, ArtikelActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed()
        }else{
            Toast.makeText(this, "Press back again to exit app", Toast.LENGTH_SHORT).show()
        }

        backPressedTime = System.currentTimeMillis()
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            //Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUser(id : String) {

        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE)

        val reference = FirebaseDatabase.getInstance().getReference()
        reference.child("users").child(id)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    if (data != null) {
                        uName.text = "Hai "+data.name+"!"
                        if (data!!.photoProfile == "" || data.photoProfile == null){
                            uPhoto.setImageResource(R.drawable.profile)
                        }else{
                            Glide.with(this@MainActivity).load(data!!.photoProfile).into(uPhoto)
                        }
                    }else{
                        Toast.makeText(this@MainActivity, "No such document", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
    }


}