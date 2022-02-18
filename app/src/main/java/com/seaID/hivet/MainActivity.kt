package com.seaID.hivet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.seaID.hivet.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef : FirebaseFirestore

    private lateinit var uName : TextView
    private lateinit var uPhoto : CircleImageView
    private lateinit var bLogout : ImageView
    private lateinit var bKonsul : ImageButton
    private lateinit var bBooking : ImageButton
    private lateinit var profile : ImageView

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
        uPhoto = findViewById(R.id.imageView3)
        bLogout = findViewById(R.id.imageLogout)
        bKonsul = findViewById(R.id.bkonsultasi)
        bBooking = findViewById(R.id.bBooking)
        profile = findViewById(R.id.imageSetting)

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
        }


        profile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))

        }


    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUser(id : String) {

        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE)

        val uidRef  = mDbRef.collection("users").document(id)

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                uName.text = "Hai "+user!!.name+"!"
                if (user!!.photoProfile == "" || user.photoProfile == null){
                    uPhoto.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(uPhoto)
                }
                //Toast.makeText(this, "{$user.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }


}