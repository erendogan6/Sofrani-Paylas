package com.erendogan6.sofranipaylas.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.erendogan6.sofranipaylas.R
import com.erendogan6.sofranipaylas.databinding.EventItemBinding
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.ui.fragment.HomeFragmentDirections
import java.util.Locale

class HomeAdapter : ListAdapter<Post, HomeAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(private val binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.eventTitle.text = post.title
            val formatter = SimpleDateFormat("dd MMMM yyyy, EEEE, HH:mm", Locale.getDefault())
            binding.eventDate.text = formatter.format(post.date.toDate())
            binding.eventDesc.text = post.description
            post.image.let {
                if (it.isNotEmpty()) {
                    Glide.with(binding.root.context).load(it).optionalCenterCrop().into(binding.eventImageView)
                }
            }
            binding.usernameTextView.text = post.hostUserName

            val iconRes = if (post.eventStatus) {
                R.drawable.open_icon
            } else {
                R.drawable.close_icon
            }
            binding.eventTitle.setCompoundDrawablesWithIntrinsicBounds(iconRes, 0, 0, 0)

            binding.root.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToPostDetailFragment(post.postID)
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
