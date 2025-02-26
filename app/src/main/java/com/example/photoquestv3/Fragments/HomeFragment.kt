package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.Views.Fragments.ProfileFragment
import com.example.photoquestv3.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var challengesVm: ChallengesViewModel
    private lateinit var vmFireStore: FireStoreViewModel
    private lateinit var postVm: PostViewModel
    private lateinit var authVm: AuthViewModel
    lateinit var adapter: PostAdapter
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModels()
        currentUserId = authVm.getCurrentUser()?.uid.toString()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(
            emptyList(),
            postVm,
            onPostTextClicked = { post -> editPostTextDialog(post) },
            onPostClicked = { post ->
                navigateToProfile(post.userid)
            })
        binding.recyclerView.adapter = adapter

        postVm.toastMessage.observe(viewLifecycleOwner) { messages ->
            messages?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        postVm.itemId.observe(viewLifecycleOwner) { itemId ->
            Log.d("HomeFragment", "Selected post id is: $itemId")
        }

        observeAndDisplayPosts()

        postVm.dataChanged.observe(viewLifecycleOwner) { dataChanged ->
            if (dataChanged) {
                adapter.notifyDataSetChanged()
            }
        }

        showDailyChallenge()
    }

    private fun showDailyChallenge() {
        challengesVm.getDailyChallenge { latestChallenge ->
            if (latestChallenge != null) {
                binding.dailyChallengeTv.text = latestChallenge.challenge
            } else {
                Log.d("HomeFragment", "No challenges available.")
            }
            if (latestChallenge?.completed == true) {
                binding.starImageView.setImageResource(R.drawable.photoquest_star)
            }
        }
    }

    fun navigateToProfile(uid: String) {
        val bundle = Bundle().apply {
            putString("uid", uid)
        }
        val profileFragment = ProfileFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, profileFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeAndDisplayPosts() {
        vmFireStore.getPostsFromFollowing(currentUserId).observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }

    fun initializeViewModels() {
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]
        vmFireStore = ViewModelProvider(this)[FireStoreViewModel::class.java]
        postVm = ViewModelProvider(requireActivity())[PostViewModel::class.java]
        authVm = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
    }


    private fun editPostTextDialog(post: Post) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Post")

        val input = EditText(requireContext())
        input.setText(post.description)
        builder.setView(input)

        builder.setPositiveButton("Update") { dialog, _ ->

            val newText = input.text.toString().trim()
            if (newText.isNotEmpty()) {
                postVm.updatePostText(post.postId, newText, onSuccess = {
                    Toast.makeText(requireContext(), "Post updated", Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(requireContext(), "Failed to update post", Toast.LENGTH_SHORT)
                        .show()
                })
            } else {
                Toast.makeText(requireContext(), "Please enter new text", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}