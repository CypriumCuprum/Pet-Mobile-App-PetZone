package com.example.petapp.viewmodel.medical_report // Adjust package as needed

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petapp.R

// 1. Define an interface for long click
interface OnImageLongClickListener {
    fun onImageLongClicked(imageUri: Uri)
}

class MedicalReportImageAdapter(
    private val imageUris: List<Pair<String?, Uri>>,
    private val onImageLongClickListener: OnImageLongClickListener // 2. Add listener to constructor
) : RecyclerView.Adapter<MedicalReportImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_medical_report, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = imageUris[position].second
        Glide.with(holder.imageView.context)
            .load(uri)
            .placeholder(R.drawable.ic_launcher_background) // Optional
            .error(R.drawable.pet) // Optional
            .into(holder.imageView)

        // 3. Set long click listener
        holder.itemView.setOnLongClickListener {
            onImageLongClickListener.onImageLongClicked(uri)
            true // Return true to indicate the event was consumed
        }
    }

    override fun getItemCount(): Int = imageUris.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewMedicalReportItem)
    }
}