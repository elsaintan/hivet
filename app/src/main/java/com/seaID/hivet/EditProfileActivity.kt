package com.seaID.hivet

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance().currentUser!!
        mDbRef = FirebaseFirestore.getInstance()


        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        var type = intent.getIntExtra("type", 0)

        if (type == 2){
            showDataUser()
        }


        binding.userImage.setOnClickListener{
            chooseImage()
        }

        binding.saveBTN.setOnClickListener {
            if (filePath.toString() == photo){
                updateDataUser()
            }else{
                deleteImage(photo.toString())
                uploadImage()
                updateDataUser()
            }
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        counter ++
        if (counter == 1){
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
    }

    private fun showDataUser(){
        val uidRef  = mDbRef.collection("users").document(mAuth.uid)
        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val user = doc.toObject(User::class.java)
                binding.nameTV.setText(user!!.name)
                binding.emailTV.setText(user!!.email)
                photo = user!!.photoProfile
                if (user!!.photoProfile == ""){
                    binding.userImage.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(user!!.photoProfile).into(binding.userImage)
                }
                Toast.makeText(this, "{$user.name}", Toast.LENGTH_SHORT).show()
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
                binding.saveBTN.visibility = View.VISIBLE
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
                    OnSuccessListener<UploadTask.TaskSnapshot>{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                        binding.saveBTN.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    OnFailureListener{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Failed "+it.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun deleteImage(photo : String) {
        val photoRef: StorageReference = storageRef.child("Image/"+mAuth.uid)
        photoRef.delete().addOnSuccessListener {
            Toast.makeText(this, "onSuccess: deleted file", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(this, "onFailure: did not delete file", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateDataUser(){
        val name = binding.nameTV.text.toString()
        val email = binding.emailTV.text.toString()
        val useredit = User(name, email, mAuth.uid, filePath.toString())

        val user = mDbRef.collection("users")
        user.document(mAuth.uid).set(useredit)
    }
}