package com.example.petapp.viewmodel.shop

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.R
import com.example.petapp.data.model.ItemTypeEntity
import com.example.petapp.data.model.getIconResId

class ItemTypeAdapter(private var dataList: List<ItemTypeEntity>) : RecyclerView.Adapter<ItemTypeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_type, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        Log.d("debug", item.name)
        holder.titleTextView.text = item.name
        holder.imageView.setImageResource(item.getIconResId())
    }

    fun updateData(newList: List<ItemTypeEntity>) {
        dataList = newList
        notifyDataSetChanged()
    }
}
