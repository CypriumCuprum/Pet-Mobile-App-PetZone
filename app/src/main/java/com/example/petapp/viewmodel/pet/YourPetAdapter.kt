package com.example.petapp.view

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
import com.example.petapp.data.model.submodel.PetReduceForHome

class YourPetAdapter :
    ListAdapter<PetReduceForHome, YourPetAdapter.PetViewHolder>(PetDiffCallback()) {

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

        fun bind(pet: PetReduceForHome) {
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
                // Example navigation to detail screen
                // val action = HomeFragmentDirections.actionHomeFragmentToPetDetailFragment(pet.id)
                // itemView.findNavController().navigate(action)
            }
        }
    }

    class PetDiffCallback : DiffUtil.ItemCallback<PetReduceForHome>() {
        override fun areItemsTheSame(
            oldItem: PetReduceForHome,
            newItem: PetReduceForHome
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PetReduceForHome,
            newItem: PetReduceForHome
        ): Boolean {
            return oldItem == newItem
        }
    }
}