package com.example.petapp.viewmodel.shop

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.ItemEntity

class ItemAdapter(private var items: List<ItemEntity>, private val onItemClick: (ItemEntity) -> Unit) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    private var originalItems: List<ItemEntity> = items.toList()

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val tvQuantity = view.findViewById<TextView>(R.id.tvQuantity)
        val tvManufacturer = view.findViewById<TextView>(R.id.chipManufacturer)
        val imgProduct = view.findViewById<ImageView>(R.id.imgProduct)
        val  tvPrice = view.findViewById<TextView>(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvDescription.text = item.description
        holder.tvQuantity.text = "Quantity: ${item.quantity}"
        holder.tvManufacturer.text = "${item.manufacturer}"
        holder.tvPrice.text = "Price: ${item.price}"
        // Load ảnh từ URL
        Log.d("debug1", item.image_url)
        Glide.with(holder.itemView.context)
            .load( "${BuildConfig.SERVER_URL}${item.image_url}")
//            .placeholder(R.drawable.ic_placeholder) // ảnh tạm khi loading
//            .error(R.drawable.ic_error) // ảnh lỗi nếu URL sai
            .into(holder.imgProduct)

        holder.itemView.setOnClickListener(){
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size


    fun updateData(newItems: List<ItemEntity>) {
        originalItems = newItems.toList()
        items = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        items = if (query.isEmpty()) {
            originalItems
        } else {
            originalItems.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }


    fun getCurrentData(): List<ItemEntity> {
        return items
    }
}
