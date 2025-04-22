package com.example.petapp.viewmodel.shop

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.R
import com.example.petapp.data.model.ItemTypeEntity
import com.example.petapp.data.model.getIconResId
import kotlin.math.log

class ItemTypeAdapter(private var dataList: List<ItemTypeEntity>, private val onItemTypeSelected: (ItemTypeEntity) -> Unit) :
    RecyclerView.Adapter<ItemTypeAdapter.ViewHolder>() {

    var selectedPosition = 0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val cardView: CardView = itemView.findViewById(R.id.cardImage)

        init {
            onItemTypeSelected(dataList[selectedPosition])
            cardView.setOnClickListener {
                Log.d("debug2", adapterPosition.toString())
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition // Dùng adapterPosition thay vì bindingAdapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemTypeSelected(dataList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shop_type, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.titleTextView.text = item.name
        holder.imageView.setImageResource(item.getIconResId())
        Log.d("debug", item.listItem[0].image_url)
        // Thay đổi màu background nếu item được chọn
        val context = holder.itemView.context
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.bg_main_color))
        } else {
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.bg_grey))
        }
    }

    fun updateData(newList: List<ItemTypeEntity>) {
        dataList = newList
        notifyDataSetChanged()
    }

}
