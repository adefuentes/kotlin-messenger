package com.asource.kotlinmessenger.activities.messages

import com.asource.kotlinmessenger.R
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.asource.kotlinmessenger.items.ChatToItem
import com.asource.kotlinmessenger.items.ChatFromItem
import com.asource.kotlinmessenger.models.ChatMessage
import com.asource.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    val TAG = "ChatLogActivity"

    companion object Factory {
        val KEY_EXTRA = "com.asource.kotlinmessenger.KEY_NAME"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        supportActionBar?.elevation = 0f
        recyclerview_chat_log.adapter = adapter

        if (intent.hasExtra(KEY_EXTRA)) {
            user = intent.getParcelableExtra<User>(KEY_EXTRA)
            supportActionBar?.title = user?.username
        }


        listenForMessages()

        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage != null) {

                    if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                        Log.d(TAG, "Entro1")
                        adapter.add(ChatFromItem(chatMessage.text, user ?: return))
                    } else {
                        val currenUser = LatestMessagesActivity.currentUser ?: return
                        Log.d(TAG, "Entro2")
                        adapter.add(ChatToItem(chatMessage.text, currenUser))
                    }

                    recyclerview_chat_log.smoothScrollToPosition(adapter.itemCount - 1)

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }



    private fun performSendMessage() {

        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user!!.uid
        //val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage).addOnSuccessListener {
            editText_chat_log.text.clear()
            Log.d(TAG, adapter.itemCount.toString())
            recyclerview_chat_log.smoothScrollToPosition(adapter.itemCount - 1)
        }
        toRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }

}
