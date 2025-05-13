package com.example.petapp.viewmodel.shop;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.BuildConfig
import com.example.petapp.R
import com.example.petapp.data.model.CartItemEntity;
import com.example.petapp.data.model.ItemEntity

import kotlin.Unit;

class CartAdapter(
    private val onIncreaseQuantity: (CartItemEntity) -> Unit,
    private val onDecreaseQuantity: (CartItemEntity) -> Unit,
    private val onItemClick: (ItemEntity) -> Unit,
    private val onItemSelectedChanged: (CartItemEntity, Boolean) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var items: List<CartItemEntity> = emptyList()
    private val selectedItems = mutableSetOf<CartItemEntity>()

    fun updateItems(newItems: List<CartItemEntity>) {
        items = newItems
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<CartItemEntity> = selectedItems.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = items[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tv_product_name)
        private val tvProductWeight = itemView.findViewById<TextView>(R.id.tv_product_weight)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvQuantity: TextView = itemView.findViewById(R.id.tv_quantity)
        private val btnDecrease: ImageView = itemView.findViewById(R.id.btn_decrease)
        private val btnIncrease: ImageView = itemView.findViewById(R.id.btn_increase)
        private val ivProductImage: ImageView = itemView.findViewById(R.id.iv_product_image)
        private val cbItemSelect: CheckBox = itemView.findViewById(R.id.cb_item_select)

        fun bind(cartItem: CartItemEntity) {
            tvProductName.text = cartItem.item.name
            // Extract weight from description or set default
            val weightText = extractWeightFromDescription(cartItem.item.description)
            tvProductWeight.text = weightText
            tvProductWeight.text = ""
            // Format price with currency
            val formattedPrice = "$ ${cartItem.item.price} x ${cartItem.quantity}"
            tvPrice.text = formattedPrice

            tvQuantity.text = cartItem.quantity.toString()

            // Load image (simplified for this example)
            // Glide.with(itemView).load(cartItem.item.imageUrl).into(ivProductImage)

            Glide.with(itemView.context)
                .load( "${BuildConfig.SERVER_URL}${cartItem.item.image_url}")
//            .placeholder(R.drawable.ic_placeholder) // ảnh tạm khi loading
//            .error(R.drawable.ic_error) // ảnh lỗi nếu URL sai
                .into(ivProductImage)

            // Checkbox setup
            cbItemSelect.isChecked = selectedItems.contains(cartItem)
            cbItemSelect.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(cartItem)
                } else {
                    selectedItems.remove(cartItem)
                }
                onItemSelectedChanged(cartItem, isChecked)
            }

            // Setup quantity buttons
            btnIncrease.setOnClickListener {
                onIncreaseQuantity(cartItem)
            }

            btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    onDecreaseQuantity(cartItem)
                }
            }

            itemView.setOnClickListener(){
                val item = ItemEntity(cartItem.item.id, cartItem.item.name, 10, cartItem.item.price, cartItem.item.description, cartItem.item.manufacturer, cartItem.item.image_url)
                onItemClick(item)
            }
        }

        private fun extractWeightFromDescription(description: String): String {
            // Example logic to extract weight from description
            // In a real app, you might have a dedicated field for this
            return when {
                description.contains("kg") -> {
                    val regex = "(\\d+(\\.\\d+)?)\\s*kg".toRegex()
                    val match = regex.find(description)
                    if (match != null) "${match.groupValues[1]}kg" else "N/A"
                }
                description.contains("g") -> {
                    val regex = "(\\d+)\\s*g".toRegex()
                    val match = regex.find(description)
                    if (match != null) "${match.groupValues[1]}g" else "N/A"
                }
                else -> description
            }
        }
    }

    fun removeSelectedItems() {
        val updatedList = items.toMutableList()
        selectedItems.forEach { selectedItem ->
            updatedList.removeIf { it.id == selectedItem.id }
        }
        items = updatedList
        selectedItems.clear()
        notifyDataSetChanged()
    }

}
