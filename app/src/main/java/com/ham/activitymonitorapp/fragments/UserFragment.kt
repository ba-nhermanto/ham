package com.ham.activitymonitorapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ham.activitymonitorapp.R
import com.ham.activitymonitorapp.data.entities.Gender
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.UserFragmentBinding
import com.ham.activitymonitorapp.other.Constants.DEFAULT_DATE
import com.ham.activitymonitorapp.view.Toaster
import com.ham.activitymonitorapp.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class UserFragment: Fragment(R.layout.user_fragment) {

    private val userViewModel: UserViewModel by viewModels()

    private var _binding: UserFragmentBinding? = null

    private val binding get() = _binding!!

    private var activeUser: User? = null

    private val toaster = Toaster()

    companion object {
        const val TAG = "USER_FRAGMENT"
    }

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
        handleButtonNewUser()
        handleButtonDeleteUser()
        handleDatePicker()

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
            saveUser(false)
        }
    }

    private fun handleButtonNewUser() {
        binding.buttonNewUser.setOnClickListener {
            saveUser(true)
        }
    }

    private fun handleButtonDeleteUser() {
        binding.buttonDeleteUser.setOnClickListener {
            deleteActiveUser()
        }
    }

    private fun saveUser(create: Boolean) {
        val id = binding.textViewUserId.text.toString().toLong()
        val name: String = binding.editTextUserName.text.toString().trim()
        val dob: Date = try {
            Date.valueOf(binding.editTextUserDob.text.toString().trim())
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            Date.valueOf(DEFAULT_DATE)
        }
        val gender: Gender = if (binding.radioButtonMale.isChecked) Gender.MALE else Gender.FEMALE
        val weight: Int? = binding.editTextUserWeight.text.toString().trim().toIntOrNull()
        val deviceId: String = binding.editTextUserDeviceId.text.toString().trim()

        if (name.isEmpty()) {
            toaster.showToast("Please enter user name", requireContext())
            return
        }

        if (weight == null) {
            toaster.showToast("Please enter valid user data", requireContext())
            return
        }

        if (deviceId.isEmpty()) {
            toaster.showToast("Please enter device id", requireContext())
            return
        }

        val user = if (create) {
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

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userViewModel.upsertUser(user)
            }
        }

        toaster.showToast("User is saved", requireContext())
    }

    private fun observeActiveUser() {
        userViewModel.activeUser.observe(viewLifecycleOwner) { user ->
            binding.activeUser = user
            this.activeUser = user
        }
    }

    private fun handleDatePicker() {
        val calendar = Calendar.getInstance()

        binding.editTextUserDob.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                binding.editTextUserDob.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    private fun deleteActiveUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                userViewModel.deleteActiveUser()
            }
        }
    }
}