package com.example.userhub.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.userhub.R
import com.example.userhub.data.local.entity.UserEntity
import com.example.userhub.databinding.ItemUserBinding

class UserAdapter : ListAdapter<UserEntity, UserAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var expandedPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            val isExpanded = position == expandedPosition
            holder.bind(user, isExpanded)

            holder.itemView.setOnClickListener {
                val prevExpandedPosition = expandedPosition
                expandedPosition = if (isExpanded) null else position

                prevExpandedPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(position)
            }
        }
    }

    class MyViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        // 👈 Ganti tipe data parameter dari UserResponseItem menjadi UserEntity
        fun bind(data: UserEntity, isExpanded: Boolean) {
            binding.tvUsername.text = data.name
            binding.tvEmail.text = data.email
            binding.tvCity.text = data.city

            binding.tvPhone.text = "Phone: ${data.phoneNumber}"
            binding.tvAddress.text = "Address: ${data.address}"
            binding.tvGender.text = "Gender: ${if (data.gender == 0) "Male" else "Female"}"

            binding.layoutDetailExpand.visibility = if (isExpanded) View.VISIBLE else View.GONE

            if (isExpanded) {
                binding.show.setImageResource(R.drawable.iconamoon_arrow_up_2)
            } else {
                binding.show.setImageResource(R.drawable.iconamoon_arrow_down_2)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UserEntity> =
            object : DiffUtil.ItemCallback<UserEntity>() {
                override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}