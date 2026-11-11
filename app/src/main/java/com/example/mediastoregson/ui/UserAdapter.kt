package com.example.mediastoregson.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mediastoregson.data.User
import com.example.mediastoregson.databinding.UserItemBinding

class UserAdapter(
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : ListAdapter<User, UserAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.binding.tvId.text = "ID: ${user.id}"
        holder.binding.tvName.text = "Nama: ${user.name}"
        holder.binding.tvEmail.text = "Email: ${user.email}"

        holder.binding.btnEdit.setOnClickListener {
            onEdit(user)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDelete(user)
        }
    }
}

