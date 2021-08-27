package com.raveendran.helpdevs.adapters

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
import com.raveendran.helpdevs.models.Todo
import kotlinx.android.synthetic.main.row_todo_item.view.*


class TodoAdapter(val context: Context) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var lastPosition = -1

    private val differCallback = object : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.todo == newItem.todo
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Todo>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_todo_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = differ.currentList[position]
        holder.itemView.apply {
            val highPriorityText = "\uD83D\uDFE2"
            val lowPriorityText = "\uD83D\uDD34"
            todoTv.text = todoItem.todo
            timeTv.text = todoItem.time
            noteTv.text = todoItem.notes
            priorityTv.text =
                if (todoItem.priority.equals("High", true)) highPriorityText else lowPriorityText
            totalProgress.progress = todoItem.progress
            progressCount.text = "${todoItem.progress}% Completed"
            setAnimation(this, position)
            setOnClickListener {
                onItemClickListener?.let { it(todoItem) }
            }
        }
    }

    private var onItemClickListener: ((Todo) -> Unit)? = null

    fun setOnItemClickListener(listener: (Todo) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.item_fall_down)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}