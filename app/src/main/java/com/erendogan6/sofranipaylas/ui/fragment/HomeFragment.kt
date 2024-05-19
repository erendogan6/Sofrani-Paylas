package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.erendogan6.sofranipaylas.adapter.HomeAdapter
import com.erendogan6.sofranipaylas.databinding.FragmentHomeBinding
import com.erendogan6.sofranipaylas.extensions.checkUserSessionAndNavigate
import com.erendogan6.sofranipaylas.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = HomeAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        checkUserSessionAndNavigate()
        setupRecyclerView()
        observePosts()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.homeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun observePosts() {
        lifecycleScope.launch {
            viewModel.posts.observe(viewLifecycleOwner) { posts ->
                adapter.submitList(posts)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPosts()
    }
}