package com.example.serenart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.serenart.R
import com.example.serenart.models.Exercise

class ExerciseAdapter(
    private val exercises: List<Exercise>,
    private val onExerciseClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivExerciseImage: ImageView = itemView.findViewById(R.id.iv_exercise_image)
        val tvExerciseTitle: TextView = itemView.findViewById(R.id.tv_exercise_title)
        val tvDuration: TextView = itemView.findViewById(R.id.tv_duration)

        fun bind(exercise: Exercise) {
            ivExerciseImage.setImageResource(exercise.imageRes)
            tvExerciseTitle.text = exercise.title
            tvDuration.text = exercise.duration

            itemView.setOnClickListener {
                onExerciseClick(exercise)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}