package com.seaID.hivet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.seaID.hivet.adapters.MessageAdapter
import com.seaID.hivet.models.Chat
import com.seaID.hivet.models.User
import java.util.ArrayList
import java.util.HashMap

class ChatActivity : AppCompatActivity() {

    private lateinit var sendMessage: ImageView
    private lateinit var userMessageInput: EditText
    private lateinit var userMessageList: RecyclerView
    private lateinit var messageAdapter: MessageAdapter

    var mAuth: FirebaseUser? = null
    var reference: DatabaseReference? = null

    var seenEventListener: ValueEventListener? = null

    var mchat: List<Chat>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sendMessage = findViewById(R.id.btn_send)
        userMessageInput = findViewById(R.id.userMessageInput)
        userMessageList = findViewById(R.id.chats)


        val userid = intent.getStringExtra("Uid")
        mAuth = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("users").child(userid.toString())

        userMessageList.setHasFixedSize(true)
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        userMessageList.setLayoutManager(manager)

        sendMessage.setOnClickListener {
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
                val user: User? = dataSnapshot.getValue(User::class.java)

                ReadMessage(mAuth!!.uid, userid)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        SeenMessage(userid.toString())
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
                    mchat.add(chat!!)
                    messageAdapter = MessageAdapter(applicationContext, mchat)
                    messageAdapter.notifyDataSetChanged()
                    userMessageList!!.adapter = messageAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun Status(status: String) {
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
    }
}