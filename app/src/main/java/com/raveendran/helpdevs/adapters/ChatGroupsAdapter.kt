package com.raveendran.helpdevs.adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.ChatGroup
import com.raveendran.helpdevs.other.Constants
import com.raveendran.helpdevs.other.SharedPrefs
import kotlinx.android.synthetic.main.row_chat_group_item.view.*


class ChatGroupsAdapter(val context: Context) :
    RecyclerView.Adapter<ChatGroupsAdapter.GroupViewHolder>() {
    private lateinit var sharedPref: SharedPreferences
    private var userName = ""

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var lastPosition = -1

    private val differCallback = object : DiffUtil.ItemCallback<ChatGroup>() {
        override fun areItemsTheSame(oldItem: ChatGroup, newItem: ChatGroup): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }

        override fun areContentsTheSame(oldItem: ChatGroup, newItem: ChatGroup): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<ChatGroup>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        sharedPref = parent.context.let { SharedPrefs.sharedPreferences(it) }
        userName = sharedPref.getString(Constants.KEY_NAME, "").toString()
        return GroupViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_chat_group_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = differ.currentList[position]
        holder.itemView.apply {
            val name = group.groupName
            val firstLetter = name[0]
            groupIconView.text = firstLetter.toString()
            groupNameTv.text = name
            lastChatTv.text =
                if (group.lastChat.isEmpty()) "Tap to Message" else "${getName(group.lastChatMemberName)}: ${group.lastChat}"
            lastChatTime.text = group.lastChatTime
            setOnClickListener {
                onItemClickListener?.let { it(group) }
            }
            setAnimation(this, position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onItemClickListener: ((ChatGroup) -> Unit)? = null

    fun setOnItemClickListener(listener: (ChatGroup) -> Unit) {
        onItemClickListener = listener
    }

    private fun getName(name: String): String {
        return if (userName == name) {
            "You"
        } else name
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.rotate_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

}