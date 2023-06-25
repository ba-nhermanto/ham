package com.ham.activitymonitorapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.databinding.ExerciseListFragmentBinding

class ExerciseListFragment: Fragment(R.layout.exercise_list_fragment) {
    private var _binding: ExerciseListFragmentBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExerciseListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

}