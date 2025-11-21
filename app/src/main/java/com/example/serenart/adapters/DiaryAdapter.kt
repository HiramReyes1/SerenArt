package com.example.serenart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serenart.R
import com.example.serenart.models.DiaryEntry

class DiaryAdapter(
    private val entries: List<DiaryEntry>,
    private val onEntryClick: (DiaryEntry) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivDrawing: ImageView = itemView.findViewById(R.id.iv_drawing)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvEntryPreview: TextView = itemView.findViewById(R.id.tv_entry_preview)
        val tvMood: TextView = itemView.findViewById(R.id.tv_mood)

        fun bind(entry: DiaryEntry) {
            tvDate.text = entry.date
            tvEntryPreview.text = entry.entryText
            tvMood.text = entry.mood

            // TODO: Cargar imagen real del dibujo
            // Por ahora usar placeholder
            ivDrawing.setImageResource(R.drawable.placeholder_exercise)

            itemView.setOnClickListener {
                onEntryClick(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_entry, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size
}