package com.seaID.hivet

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.seaID.hivet.models.User
import com.seaID.hivet.databinding.ActivityEditProfileBinding
import java.io.IOException
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private var filePath : Uri? = null
    private final val PICK_IMAGE_REQUEST : Int = 2020

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var mAuth: FirebaseUser
    private lateinit var mDbRef: FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var storageRef : StorageReference

    private var photo : String ?= null
    var counter : Int = 0
    var type : Int ?= null

    companion object {
        private const val STORAGE_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            STORAGE_PERMISSION_CODE
        )

        mAuth = FirebaseAuth.getInstance().currentUser!!
        mDbRef = FirebaseFirestore.getInstance()


        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val type = intent.getIntExtra("type", 0)
        val email = intent.getStringExtra("email")
        val uid = mAuth.uid

        if (type == 2){
            binding.saveBTN.visibility = View.VISIBLE
            showDataUser()
        }else if (type == 1){
            binding.saveBTN.visibility = View.VISIBLE
            binding.emailTV.setText(email)
        }



        binding.userImage.setOnClickListener{
            chooseImage()
        }

        binding.saveBTN.setOnClickListener {

            if (filePath.toString() == null){
                updateDataUser()
            }else{
                uploadImage()
                updateDataUser()
            }
            binding.progressBar.visibility = View.VISIBLE
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this, UserProfileActivity::class.java))
        finish()
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDataUser(){
        val uidRef  = mDbRef.collection("users").document(mAuth.uid)
        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                binding.nameTV.setText(user!!.name)
                binding.emailTV.setText(user.email)
                photo = user.photoProfile
                if (user.photoProfile == "" || user.photoProfile == null){
                    binding.userImage.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(binding.userImage)
                }
            } else {
                Toast.makeText(this, "No such document ", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }

    private fun chooseImage(){
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null){
            filePath = data!!.data
            try {
                var bitmap:Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                binding.userImage.setImageBitmap(bitmap)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        if(filePath != null){
            var ref:StorageReference = storageRef.child("Image/"+mAuth.uid)
            ref.putFile(filePath!!)
                .addOnSuccessListener {
                        binding.progressBar.visibility = View.GONE
                        binding.saveBTN.visibility = View.GONE
                        photo = filePath.toString()
                        updateFoto(photo.toString())
                }
                .addOnFailureListener {
                    OnFailureListener{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Failed "+it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            //photo = ref.downloadUrl.toString()
            Toast.makeText(this, "Gambar "+ ref.downloadUrl, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFoto(photo: String) {
        mDbRef.collection("users").document(mAuth.uid).update("photoProfile", photo)
    }

    private fun deleteImage(photo : String) {
        val photoRef: StorageReference = storageRef.child("Image/"+mAuth.uid)
        photoRef.delete().addOnSuccessListener {
            //Toast.makeText(this, "onSuccess: deleted file", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, "onFailure: did not delete file", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDataUser(){
        val name = binding.nameTV.text.toString()
        val email = binding.emailTV.text.toString()
        //val useredit = User(name, email, mAuth.uid, photo)

        Toast.makeText(this, "Gambar "+ photo, Toast.LENGTH_SHORT).show()

        val user = mDbRef.collection("users")
        user.document(mAuth.uid).update("name", name, "email", email)
        startActivity(Intent(this, MainActivity::class.java))
    }
}