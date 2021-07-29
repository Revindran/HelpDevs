package com.raveendran.helpdevs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raveendran.helpdevs.R
import com.raveendran.helpdevs.models.Dev
import kotlinx.android.synthetic.main.row_dev_item.view.*

class DevAdapter : RecyclerView.Adapter<DevAdapter.DevViewHolder>() {

    inner class DevViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Dev>() {
        override fun areItemsTheSame(oldItem: Dev, newItem: Dev): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dev, newItem: Dev): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Dev>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevViewHolder {
        return DevViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_dev_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DevViewHolder, position: Int) {
        val devItem = differ.currentList[position]
        holder.itemView.apply {

            Glide.with(context)
                .load(devItem.image)
                .centerCrop()
                .into(imageView)

            textView.text = devItem.title
            setOnClickListener {
                onItemClickListener?.let { it(devItem) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Dev) -> Unit)? = null

    fun setOnItemClickListener(listener: (Dev) -> Unit) {
        onItemClickListener = listener
    }
}