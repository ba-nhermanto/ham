package com.ham.activitymonitorapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.UserListFragmentBinding
import com.ham.activitymonitorapp.view.adapters.UserAdapter
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class UserListFragment: Fragment(R.layout.user_list_fragment) {

    private val userViewModel: UserViewModel by viewModels()
    private var _binding: UserListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleButtonUserListOnClick()

        userViewModel.getUsers()
        showListOfUsers()
    }

    private fun handleButtonUserListOnClick() {
        binding.btnGoToUserProfile.setOnClickListener {
            findNavController().navigate(R.id.action_userListFragment_to_userFragment)
        }
    }

    private fun showListOfUsers() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            initRecyclerView(users)
            Log.d(tag, users.map{user -> user.userId}.toString())
        }
    }

    private fun initRecyclerView(userList: List<User>) {
        userAdapter = UserAdapter(userList) { user, _ ->
            runBlocking {
                userViewModel.setActiveUser(user.userId)
            }
        }
        recyclerView = binding.recyclerViewUsers

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }
    }

}