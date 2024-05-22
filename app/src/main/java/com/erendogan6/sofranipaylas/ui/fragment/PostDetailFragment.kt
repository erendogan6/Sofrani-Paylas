package com.erendogan6.sofranipaylas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.erendogan6.sofranipaylas.R
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

        setupObservers()
        setupJoinButton()
        viewModel.loadPostDetails(args.postId)
    }

    private fun setupObservers() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let { bindPostDetails(it) }
        }

        viewModel.joinStatus.observe(viewLifecycleOwner) { result ->
            handleJoinStatus(result)
        }

        viewModel.isAlreadyJoined.observe(viewLifecycleOwner) { isJoined ->
            updateJoinButtonState(isJoined)
        }

        viewModel.address.observe(viewLifecycleOwner) { address ->
            binding.detailAddress.text = address
        }

        viewModel.formattedDate.observe(viewLifecycleOwner) { formattedDate ->
            binding.detailDate.text = formattedDate
        }
    }

    private fun setupJoinButton() {
        binding.joinButton.setOnClickListener {
            viewModel.joinPost(args.postId)
        }
    }

    private fun bindPostDetails(post: Post) {
        binding.detailTitle.text = post.title
        binding.detailDescription.text = post.description
        binding.detailMaxParticipants.text = "Maksimum Kişi Sayısı: " + post.maxParticipants.toString()
        Glide.with(this).load(post.image).into(binding.detailImage)

        binding.detailAddress.setOnClickListener {
            val action = PostDetailFragmentDirections.actionPostDetailFragmentToMapFragment(post.latitude.toFloat(), post.longitude.toFloat())
            findNavController().navigate(action)
        }
    }

    private fun handleJoinStatus(result: Result<Boolean>) {
        result.onSuccess {
            showToast("Başarıyla katıldınız!")
            navigateToHome()
        }.onFailure { e ->
            showToast(e.message ?: "Katılım sırasında bir hata oluştu.")
        }
    }

    private fun updateJoinButtonState(isJoined: Boolean) {
        binding.joinButton.isEnabled = !isJoined
        binding.joinButton.text = if (isJoined) "Bu etkinliğe katıldınız" else "Katıl"
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_postDetailFragment_to_homeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
