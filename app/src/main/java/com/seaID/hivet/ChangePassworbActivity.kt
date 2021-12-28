package com.seaID.hivet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.seaID.hivet.databinding.ActivityChangePassworbBinding

class ChangePassworbActivity : AppCompatActivity() {

    private lateinit var cpBinding: ActivityChangePassworbBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cpBinding = ActivityChangePassworbBinding.inflate(layoutInflater)
        val view = cpBinding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        cpBinding.button.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        if (cpBinding.cPassword.text.isNotEmpty() && cpBinding.nPassword.text.isNotEmpty() && cpBinding.confirmPassword.text.isNotEmpty()){
            if (cpBinding.nPassword.toString().equals(cpBinding.confirmPassword.toString())){
                val user = auth.currentUser
                val credential = EmailAuthProvider
                    .getCredential(user?.email!!, cpBinding.cPassword.toString())

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Berhasil ganri apssworb", Toast.LENGTH_SHORT).show()
                            user!!.updatePassword(cpBinding.confirmPassword.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Passworf change successdully", Toast.LENGTH_SHORT).show()
                                        auth.signOut()
                                        startActivity(Intent(this, SignInActivity::class.java))
                                        finish()
                                    }else{
                                        Toast.makeText(this, "GAGALLLLLLLL!!!!!!!!", Toast.LENGTH_SHORT).show()
                                    }
                                }

                        }
                    }
            }else{
                Toast.makeText(this, "Password mismatching.", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()
        }
    }
}