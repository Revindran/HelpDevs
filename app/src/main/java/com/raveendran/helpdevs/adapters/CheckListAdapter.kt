package com.raveendran.helpdevs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.TodoCheckList
import kotlinx.android.synthetic.main.row_check_list_item.view.*

class CheckListAdapter(val context: Context) :
    RecyclerView.Adapter<CheckListAdapter.ListViewHolder>() {
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var lastPosition = -1
    private val differCallback = object : DiffUtil.ItemCallback<TodoCheckList>() {
        override fun areItemsTheSame(oldItem: TodoCheckList, newItem: TodoCheckList): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TodoCheckList, newItem: TodoCheckList): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<TodoCheckList>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_check_list_item,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val listItem = differ.currentList[position]
        holder.itemView.apply {
            checkTitleTv.text = listItem.title
            createdTime.text = listItem.createdTime
            if (listItem.checked) {
                checkStatusTv.text = "Completed"
                checkedView.visibility = View.VISIBLE
                uncheckedView.visibility = View.GONE
            } else {
                checkStatusTv.text = "Pending"
                checkedView.visibility = View.GONE
                uncheckedView.visibility = View.VISIBLE
            }
            setAnimation(this, position)
            checkedView.setOnClickListener {
                onItemClickListener?.let { it(listItem) }
            }
            uncheckedView.setOnClickListener {
                onItemClickListener?.let { it(listItem) }
            }
        }
    }

    private var onItemClickListener: ((TodoCheckList) -> Unit)? = null

    fun setOnItemClickListener(listener: (TodoCheckList) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.recycler_fall_down_anim)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}