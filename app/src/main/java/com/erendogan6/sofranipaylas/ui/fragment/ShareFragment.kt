package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.erendogan6.sofranipaylas.databinding.FragmentShareBinding
import com.erendogan6.sofranipaylas.viewmodel.ShareViewModel

class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val shareViewModel = ViewModelProvider(this).get(ShareViewModel::class.java)

        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}