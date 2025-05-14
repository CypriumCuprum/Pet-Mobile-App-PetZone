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

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1

sealed class ListItem {
    data class HeaderItem(val monthYear: String) : ListItem()
    data class ReportItem(val report: MedicalReportExtend) : ListItem()
}

interface OnReportItemClickListener {
    fun onReportItemClicked(reportId: String)
}

class MedicalReportListAdapter(
    private var items: List<ListItem>,
    private val itemClickListener: OnReportItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerTextView: TextView =
            itemView.findViewById(R.id.textViewMonthHeader)
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        val petTextView: TextView = itemView.findViewById(R.id.textViewPet)
        val veterinarianTextView: TextView = itemView.findViewById(R.id.textViewVeterinarian)
        val hospitalTextView: TextView = itemView.findViewById(R.id.textViewHospital)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = items[position]
                    if (item is ListItem.ReportItem) {
                        itemClickListener.onReportItemClicked(item.report.id)
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
                throw IllegalArgumentException("Invalid item type at position $position")
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
                )
                HeaderViewHolder(view)
            }

            VIEW_TYPE_ITEM -> {
                val view = inflater.inflate(
                    R.layout.item_medical_report_card,
                    parent,
                    false
                )
                ItemViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
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
                itemHolder.petTextView.text = report.petName
                itemHolder.veterinarianTextView.text = report.veterinarian
                itemHolder.hospitalTextView.text = report.hospital

                try {
                    val date = inputDateFormat.parse(report.createdAt)
                    if (date != null) {
                        itemHolder.dateTextView.text = displayDateFormat.format(date)
                    } else {
                        itemHolder.dateTextView.text = "N/A"
                    }
                } catch (e: ParseException) {
                    itemHolder.dateTextView.text = report.createdAt
                    e.printStackTrace()
                }
            }

            else -> {}
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ListItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}