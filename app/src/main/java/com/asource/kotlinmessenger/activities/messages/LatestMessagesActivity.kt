package com.asource.kotlinmessenger.activities.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.asource.kotlinmessenger.R
import com.asource.kotlinmessenger.activities.registerlogin.RegisterActivity
import com.asource.kotlinmessenger.items.LatestMessageRow
import com.asource.kotlinmessenger.models.ChatMessage
import com.asource.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages_activity.*

class LatestMessagesActivity : AppCompatActivity(), ValueEventListener {

    companion object {
        var currentUser: User? = null
    }

    val adapter = GroupAdapter<ViewHolder>()
    val latestMessagesMap = HashMap<String, ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages_activity)
        verifyUserIsLoggedIn()

        recyclerview_latestmessages.adapter = adapter
        recyclerview_latestmessages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        supportActionBar?.elevation = 0f

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra(ChatLogActivity.KEY_EXTRA, (item as LatestMessageRow).chatPartnerUser)
            startActivity(intent)
        }

        fetchCurrentUser()
        listenForLatestMessages()



    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("ChatLogActivity", uid)
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addValueEventListener(this)
    }

    override fun onCancelled(p0: DatabaseError) {
    }

    override fun onDataChange(p0: DataSnapshot) {
        if (p0.exists()) {
            currentUser = p0.getValue(User::class.java)
            Log.d("ChatLogActivity", p0.exists().toString())
        } else {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }


}
