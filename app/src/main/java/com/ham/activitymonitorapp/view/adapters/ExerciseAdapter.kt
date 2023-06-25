package com.ham.activitymonitorapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.databinding.ItemExerciseBinding

class ExerciseAdapter(
    private val dataSet: List<Exercise>,
    private val onExerciseClick: ((Exercise, View?) -> Unit)?) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemExerciseBinding,
        private val onExerciseClick: ((Exercise, View?) -> Unit)?) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exercise: Exercise) {
            val minutes = exercise.duration / 60
            val seconds = exercise.duration % 60
            val duration = String.format("%02d:%02d", minutes, seconds)
            binding.cardStartDate.setValue(exercise.startTime.toString())
            binding.cardAverageHrBpm.setValue(exercise.averageHrBpm.toString())
            binding.cardDuration.setValue(duration)
            binding.cardHighestHr.setValue(exercise.maxHrBpm.toString())
            binding.cardMinHr.setValue(exercise.minHrBpm.toString())
            binding.cardCaloriesBurned.setValue(exercise.caloriesBurned.toString())

            binding.tvExerciseId.text = exercise.exerciseId.toString()

            itemView.setOnClickListener { view ->
                onExerciseClick?.invoke(exercise, view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onExerciseClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}


