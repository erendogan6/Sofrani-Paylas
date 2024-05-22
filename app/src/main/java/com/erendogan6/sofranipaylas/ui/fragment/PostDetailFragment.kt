package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.erendogan6.sofranipaylas.databinding.FragmentPostDetailBinding
import com.erendogan6.sofranipaylas.model.Post
import com.erendogan6.sofranipaylas.viewmodel.PostDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPostDetails(args.postId)

        viewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let { bindPost(it) }
        }

        binding.joinButton.setOnClickListener {
            viewModel.joinPost(args.postId)
        }
    }

    private fun bindPost(post: Post) {
        binding.detailTitle.text = post.title
        binding.detailDate.text = post.date.toDate().toString()
        binding.detailDescription.text = post.description
        Glide.with(this).load(post.image).into(binding.detailImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}