package com.ham.activitymonitorapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.UserFragmentBinding
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.sql.Date


@AndroidEntryPoint
class UserFragment: Fragment(R.layout.user_fragment) {

    private val userViewModel: UserViewModel by viewModels()
    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!
    private var activeUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentBinding.inflate(inflater, container, false)
        observeActiveUser()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleButtonUserListOnClick()
        handleButtonSaveUser()

        this.activeUser = runBlocking{
            userViewModel.getActiveUser()
        }

        binding.activeUser = this.activeUser
    }

    private fun handleButtonUserListOnClick() {
        binding.btnGoToUserList.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_userListFragment)
        }
    }

    private fun handleButtonSaveUser() {
        binding.buttonSaveUser.setOnClickListener {
            saveUser()
        }
    }

    private fun saveUser() {
        val id = binding.textViewUserId.text.toString().toLong()
        val name: String = binding.editTextUserName.text.toString().trim()
        val dob: Date = Date.valueOf(binding.editTextUserDob.text.toString().trim())
        val gender: Gender = if (binding.radioButtonMale.isChecked) Gender.MALE else Gender.FEMALE
        val weight: Int = binding.editTextUserWeight.text.toString().trim().toInt()
        val deviceId: String = binding.editTextUserDeviceId.text.toString().trim()

        val user = if (id == 0L) {
            User(
                username = name,
                weight = weight,
                dateOfBirth = dob,
                gender = gender,
                deviceId = deviceId
            )
        } else {
            User(
                userId = id,
                username = name,
                weight = weight,
                dateOfBirth = dob,
                gender = gender,
                deviceId = deviceId
            )
        }

        runBlocking {
            userViewModel.upsertUser(user)
        }
        showToast("User with userId ${binding.activeUser.userId} is saved")
    }

    private fun observeActiveUser() {
        userViewModel.activeUser.observe(viewLifecycleOwner) { user ->
            Log.d(tag, user.toString())
            binding.activeUser = user
            this.activeUser = user
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}