package com.example.petapp.view.medical_report

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petapp.R
import com.example.petapp.data.model.submodel.MedicalReportExtend
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// Define view type constants (can be inside or outside the adapter)
private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1

// Sealed class to represent different list item types (can be inside or outside)
sealed class ListItem {
    data class HeaderItem(val monthYear: String) : ListItem() // e.g., "05/2024"
    data class ReportItem(val report: MedicalReportExtend) : ListItem()
}


class MedicalReportListAdapter(
    private var items: List<ListItem> // Use the sealed class list
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // SimpleDateFormat for parsing the input date string (adjust if format is different)
    // IMPORTANT: Ensure MedicalReportExtend.createdAt matches this format!
    private val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // SimpleDateFormat for displaying only day/month in the item card
    private val displayDateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    // --- ViewHolder for Header ---
    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTextView: TextView =
            itemView.findViewById(R.id.textViewMonthHeader) // Add this ID to your header layout
    }

    // --- ViewHolder for Report Item ---
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val petTextView: TextView = itemView.findViewById(R.id.textViewPet)
        val veterinarianTextView: TextView = itemView.findViewById(R.id.textViewVeterinarian)
        val hospitalTextView: TextView = itemView.findViewById(R.id.textViewHospital)

        // Add click listener if needed
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    if (item is ListItem.ReportItem) {
                        // pass medical report id to the next fragment
                        val reportId = item.report.id
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ListItem.HeaderItem -> VIEW_TYPE_HEADER
            is ListItem.ReportItem -> VIEW_TYPE_ITEM
            else -> {
                throw IllegalArgumentException("Invalid item type")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(
                    R.layout.item_header_for_medical_report_list,
                    parent,
                    false
                ) // Use your header layout file
                HeaderViewHolder(view)
            }

            VIEW_TYPE_ITEM -> {
                val view = inflater.inflate(
                    R.layout.item_medical_report_card,
                    parent,
                    false
                ) // Use your item layout file
                ItemViewHolder(view)
            }


            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val currentItem = items[position]) {
            is ListItem.HeaderItem -> {
                val headerHolder = holder as HeaderViewHolder
                headerHolder.headerTextView.text = currentItem.monthYear
            }

            is ListItem.ReportItem -> {
                val itemHolder = holder as ItemViewHolder
                val report = currentItem.report

                itemHolder.titleTextView.text = report.title
                itemHolder.petTextView.text = report.petName // Assuming petName is correct
                itemHolder.veterinarianTextView.text = report.veterinarian
                itemHolder.hospitalTextView.text = report.hospital

                // Parse and Format Date for display (dd/MM)
                try {
                    val date = inputDateFormat.parse(report.createdAt)
                    if (date != null) {
                        itemHolder.dateTextView.text = displayDateFormat.format(date)
                    } else {
                        itemHolder.dateTextView.text = "N/A" // Or keep original string
                    }
                } catch (e: ParseException) {
                    // Handle parsing error, maybe show the original string or an error indicator
                    itemHolder.dateTextView.text = report.createdAt // Fallback
                    e.printStackTrace()
                }
            }

            else -> {}
        }
    }

    override fun getItemCount(): Int = items.size

    // Function to update the list data (useful when filtering or loading new data)
    fun updateData(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }
}