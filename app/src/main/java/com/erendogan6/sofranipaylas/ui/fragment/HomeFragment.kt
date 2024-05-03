package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.sofranipaylas.adapter.HomeAdapter
import com.erendogan6.sofranipaylas.databinding.FragmentHomeBinding
import com.erendogan6.sofranipaylas.model.Event

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val list: ArrayList<Event> = arrayListOf()
        list.add(Event(null, "test", "1", "true", "2", "yok", null, 5, listOf(), "Selam"))
        binding.homeRecyclerView.adapter = HomeAdapter(list)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}