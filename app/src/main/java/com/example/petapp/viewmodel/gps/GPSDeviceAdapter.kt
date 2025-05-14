package com.example.petapp.viewmodel.gps

import android.annotation.SuppressLint
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
import com.example.petapp.data.model.GPSEntity
import com.example.petapp.data.repository.PetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GPSDeviceAdapter(
    private val petRepository: PetRepository,
    private val onItemClick: (GPSEntity) -> Unit
) : ListAdapter<GPSEntity, GPSDeviceAdapter.GPSDeviceViewHolder>(GPSDeviceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GPSDeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gps_device, parent, false)
        return GPSDeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: GPSDeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }

    inner class GPSDeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewSmartCollarTitle)
        private val batteryPercentage: TextView =
            itemView.findViewById(R.id.textViewBatteryPercentage)
        private val connectionStatus: TextView =
            itemView.findViewById(R.id.textViewConnectionStatus)
        private val fromTextView: TextView = itemView.findViewById(R.id.textViewFrom)
        private val collarImageView: ImageView = itemView.findViewById(R.id.imageViewCollar)
        private val coroutineScope = CoroutineScope(Dispatchers.Main)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(device: GPSEntity) {
            titleTextView.text = device.name
            batteryPercentage.text = device.battery.toString() + "%"
            connectionStatus.text = device.status

            coroutineScope.launch {
                val pet = withContext(Dispatchers.IO) {
                    petRepository.getPetById(device.petId)
                }

                pet?.let { petEntity ->
                    val petName = petEntity.name
                    if (petName.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            fromTextView.text = "From $petName"
                            fromTextView.visibility = View.VISIBLE
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            fromTextView.visibility = View.GONE
                        }
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        fromTextView.visibility = View.GONE
                    }
                }
            }

            // Load device image if available
            if (!device.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(device.imageUrl)
                    .placeholder(R.drawable.accessories1)
                    .error(R.drawable.accessories1)
                    .into(collarImageView)
            } else {
                collarImageView.setImageResource(R.drawable.accessories1)
            }
        }
    }

    class GPSDeviceDiffCallback : DiffUtil.ItemCallback<GPSEntity>() {
        override fun areItemsTheSame(oldItem: GPSEntity, newItem: GPSEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GPSEntity, newItem: GPSEntity): Boolean {
            return oldItem == newItem
        }
    }
}