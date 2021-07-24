package com.raveendran.helpdevs.adapters

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.Chat
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import kotlinx.android.synthetic.main.row_chat_item.view.*
import java.text.SimpleDateFormat

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private lateinit var sharedPref: SharedPreferences

    private var userName = ""


    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Chat>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        sharedPref = parent.context.let { SharedPrefs.sharedPreferences(it) }!!

        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_chat_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = differ.currentList[position]
        holder.itemView.apply {
            receivedTv.text = chat.text
            senderTv.text = chat.text

            var spf = SimpleDateFormat("MMM dd, yyyy hh:mm a")
            val newDate = spf.parse(chat.time)
            spf = SimpleDateFormat("hh:mm a")
            val time = spf.format(newDate)

            timeText.text = time
            receiverTimeText.text = time

            if (userName.equals(chat.name, true)) {
                outView.visibility = View.VISIBLE
                inView.visibility = View.GONE
                receiverTimeText.visibility = View.GONE
                timeText.visibility = View.VISIBLE
            } else {
                inView.visibility = View.VISIBLE
                outView.visibility = View.GONE
                receiverTimeText.visibility = View.VISIBLE
                timeText.visibility = View.GONE
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}