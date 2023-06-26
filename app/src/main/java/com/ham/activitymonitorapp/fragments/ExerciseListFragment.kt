package com.ham.activitymonitorapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.Exercise
import com.ham.activitymonitorapp.databinding.ExerciseListFragmentBinding
import com.ham.activitymonitorapp.view.adapters.ExerciseAdapter
import com.ham.activitymonitorapp.viewmodels.ExerciseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExerciseListFragment: Fragment(R.layout.exercise_list_fragment) {

    private var _binding: ExerciseListFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private val exerciseViewModel: ExerciseViewModel by viewModels()

    private lateinit var exerciseAdapter: ExerciseAdapter

    companion object {
        const val TAG = "EXERCISE_LIST_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExerciseListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exerciseViewModel.currentExercisesList.value?.let { initRecyclerView(it) }
        observeExerciseList()
    }

    private fun observeExerciseList() {
        exerciseViewModel.currentExercisesList.observe(viewLifecycleOwner) { newExercisesList ->
            Log.d(TAG, newExercisesList.toString())
            initRecyclerView(newExercisesList)
        }
    }

    private fun initRecyclerView(exerciseList: List<Exercise>) {
        exerciseAdapter = ExerciseAdapter(exerciseList) { _, _ ->

        }
        recyclerView = binding.recyclerViewExercises

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exerciseAdapter
        }
    }

}