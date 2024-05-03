package com.erendogan6.sofranipaylas.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erendogan6.sofranipaylas.databinding.EventItemBinding
import com.erendogan6.sofranipaylas.model.Event

class HomeAdapter(val list: ArrayList<Event>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: EventItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EventItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.eventTitle.text = list.get(position).title
    }

}