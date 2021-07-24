package com.raveendran.helpdevs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.ChatGroup
import kotlinx.android.synthetic.main.row_chat_group_item.view.*

class ChatGroupsAdapter : RecyclerView.Adapter<ChatGroupsAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

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
            groupNameTv.text = group.groupName

            setOnClickListener {
                onItemClickListener?.let { it(group) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onItemClickListener: ((ChatGroup) -> Unit)? = null

    fun setOnItemClickListener(listener: (ChatGroup) -> Unit) {
        onItemClickListener = listener
    }

}