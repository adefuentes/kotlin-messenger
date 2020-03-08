package com.asource.kotlinmessenger.items

import android.util.Log
import com.asource.kotlinmessenger.R
import com.asource.kotlinmessenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    val TAG = "ChatItem"
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Entroooo")
        viewHolder.itemView.textView_chat_to_row.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_chat_to_row)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}