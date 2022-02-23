package com.seaID.hivet

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.seaID.hivet.adapters.MessageAdapter
import com.seaID.hivet.models.Chat
import com.seaID.hivet.models.User
import com.seaID.hivet.models.drh
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var sendMessage: ImageView
    private lateinit var userMessageInput: EditText
    private lateinit var userMessageList: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var textName: TextView
    private lateinit var fotoProfile: ImageView
    private lateinit var tanggal: TextView
    private lateinit var konsul: TextView

    private lateinit var mDbRef: FirebaseFirestore

    var mAuth: FirebaseUser? = null
    var reference: DatabaseReference? = null

    var seenEventListener: ValueEventListener? = null

    var mchat: List<Chat>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sendMessage = findViewById(R.id.btn_send)
        userMessageInput = findViewById(R.id.userMessageInput)
        userMessageList = findViewById(R.id.chats)
        textName = findViewById(R.id.textName)
        fotoProfile = findViewById(R.id.fotoProfile)
        tanggal = findViewById(R.id.tanggal)
        konsul = findViewById(R.id.konsul)


        val userid = intent.getStringExtra("Uid")
        mAuth = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("drh").child(userid.toString())

        userMessageList.setHasFixedSize(true)
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        userMessageList.setLayoutManager(manager)

        sendMessage.setOnClickListener {
            //Toast.makeText(this, userid, Toast.LENGTH_SHORT).show()
            val msg = userMessageInput.getText().toString()
            if (!msg.isEmpty()) {
                SendMessage(mAuth!!.uid, userid.toString(), msg)
            } else {
                Toast.makeText(
                    this@ChatActivity,
                    "You can't send empty message",
                    Toast.LENGTH_SHORT
                ).show()
            }
            userMessageInput.setText("")
        }

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: drh? = dataSnapshot.getValue(drh::class.java)

                ReadMessage(mAuth!!.uid, userid)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        SeenMessage(userid.toString())

        showDataVet(userid)
        //showDataVet(userid)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDataVet(userid: String?) {

        val current = LocalDateTime.now()
        val simpleDateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val formatted = current.format(simpleDateFormat)
        tanggal.text = formatted

        mDbRef = FirebaseFirestore.getInstance()
        val uidRef  = mDbRef.collection("drh").document(userid.toString())

        uidRef.get().addOnSuccessListener { doc ->
            if (doc != null) {
                val drh = doc.toObject(drh::class.java)
                textName.text = drh!!.Name
                if (drh!!.photoProfile == ""){
                    fotoProfile.setImageResource(R.drawable.profile)
                }else{
                    Glide.with(this).load(drh!!.photoProfile).into(fotoProfile)
                }
                //Toast.makeText(this, "{$user.name}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "get failed with "+exception, Toast.LENGTH_SHORT).show()
        }
    }

    private fun SeenMessage(userid: String) {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        seenEventListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (sp in dataSnapshot.children) {
                    val chat: Chat? = sp.getValue(Chat::class.java)
                    if (chat?.getReceiver().equals(mAuth!!.uid) && chat?.getSender().equals(userid)) {
                        val hm = HashMap<String, Any>()
                        hm["isseen"] = true
                        sp.ref.updateChildren(hm)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun SendMessage(sender: String, receiver: String, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val hashMap = HashMap<String, Any>()
        hashMap["sender"] = sender
        hashMap["receiver"] = receiver
        hashMap["message"] = message
        hashMap["isseen"] = false
        reference.child("Chats").push().setValue(hashMap)
    }

    fun ReadMessage(myid: String?, userid: String?) {
        val mchat = ArrayList<Chat>()
        val reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mchat.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat: Chat? = snapshot.getValue(Chat::class.java)
                    if (chat?.getReceiver().equals(userid) && chat?.getSender().equals(mAuth!!.uid)){
                        mchat.add(chat!!)
                    }
                    messageAdapter = MessageAdapter(applicationContext, mchat)
                    messageAdapter.notifyDataSetChanged()
                    userMessageList!!.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    /**private fun Status(status: String) {
        reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth!!.uid)
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        Status("online")
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenEventListener!!)
        Status("offline")
    }**/
}