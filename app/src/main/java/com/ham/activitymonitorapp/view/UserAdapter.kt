package com.ham.activitymonitorapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ham.activitymonitorapp.data.entities.User
import com.ham.activitymonitorapp.databinding.ItemUserBinding

class UserAdapter(
    private val dataSet: List<User>,
    private val onUserClick: ((User, View?) -> Unit)?) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemUserBinding,
        private val onUserClick: ((User, View?) -> Unit)?) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            val id = user.userId
            val name = user.username
            val concatIdAndName = "$id $name"
            binding.textViewName.text = concatIdAndName

            itemView.setOnClickListener { view ->
                onUserClick?.invoke(user, view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onUserClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount() = dataSet.size
}


