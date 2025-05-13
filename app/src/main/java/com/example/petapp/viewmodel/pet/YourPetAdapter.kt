package com.example.petapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.R
import com.example.petapp.data.model.PetEntity
import com.example.petapp.view.petHealth.PetHealth

class YourPetAdapter :
    ListAdapter<PetEntity, YourPetAdapter.PetViewHolder>(PetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_your_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val petImage: ImageView = itemView.findViewById(R.id.pet_image)
        private val petName: TextView = itemView.findViewById(R.id.pet_name)

        fun bind(pet: PetEntity) {
            petName.text = pet.name
            //debug
            println("Binding pet: ${pet.name} with ID: ${pet.id}")

            if (!pet.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(pet.imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(petImage)
            } else {
                println("Pet image URL is empty for pet ID: ${pet.id}")
//                petImage.setBackgroundResource(R.drawable.profile)
            }

            // Optional: Set click listener to navigate to pet details
            itemView.setOnClickListener {
                executeOnClickPet(pet)
            }
        }

        private fun executeOnClickPet(pet: PetEntity) {
            val context = itemView.context
            val activity = context as? AppCompatActivity ?: return
            val toolbar = activity.findViewById<View>(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
            val petHealthFragment = PetHealth()
            val bundle = Bundle().apply {
                putString("petId", pet.id) // hoặc putInt nếu id là Int
            }
            petHealthFragment.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, petHealthFragment) // fragment_container là ID của container trong activity_main.xml
                .addToBackStack(null) // Cho phép back lại fragment trước đó
                .commit()
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