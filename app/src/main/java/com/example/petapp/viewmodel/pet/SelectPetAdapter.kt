package com.example.petapp.viewmodel.pet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.R
import com.example.petapp.data.model.PetEntity
import com.google.android.material.card.MaterialCardView

class SelectPetAdapter(private val onPetSelected: (PetEntity) -> Unit) :
    ListAdapter<PetEntity, SelectPetAdapter.PetViewHolder>(PetDiffCallback()) {

    // Track the currently selected item position
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_your_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    fun selectPet(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position

        // Update the previous selected item (if any)
        if (previousSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelected)
        }

        // Update the newly selected item
        notifyItemChanged(selectedPosition)

        // Notify the fragment of the selection
        onPetSelected(getItem(position))
    }

    // Method to get the currently selected pet
    fun getSelectedPet(): PetEntity? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            getItem(selectedPosition)
        } else {
            null
        }
    }

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val petImage: ImageView = itemView.findViewById(R.id.pet_image)
        private val petName: TextView = itemView.findViewById(R.id.pet_name)
        private val petContainer = itemView.findViewById<MaterialCardView>(R.id.pet_image_card)

        fun bind(pet: PetEntity, isSelected: Boolean) {
            petName.text = pet.name

            // Load the pet image using Glide
            if (pet.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(petImage)
            } else {
                petImage.setImageResource(R.drawable.profile)
            }

            // Apply selection styling
            if (isSelected) {
                petContainer.strokeColor =
                    itemView.context.resources.getColor(R.color.bg_button_pressed, null)
                petName.setTextColor(
                    itemView.context.resources.getColor(
                        android.R.color.holo_blue_dark,
                        null
                    )
                )
            } else {
                petContainer.setBackgroundResource(android.R.color.transparent)
                petName.setTextColor(
                    itemView.context.resources.getColor(
                        android.R.color.black,
                        null
                    )
                )
            }

            // Set click listener
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectPet(position)
                }
            }
        }
    }

    class PetDiffCallback : DiffUtil.ItemCallback<PetEntity>() {
        override fun areItemsTheSame(
            oldItem: PetEntity,
            newItem: PetEntity
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PetEntity,
            newItem: PetEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}