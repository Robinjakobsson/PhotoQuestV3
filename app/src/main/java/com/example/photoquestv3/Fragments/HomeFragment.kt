package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
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
    lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]
        vmFireStore = ViewModelProvider(this)[FireStoreViewModel::class.java]
        postVm = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        postVm.itemId.observe(viewLifecycleOwner) { itemId ->
            Log.d("HomeFragment", "Selected post id is: $itemId")
        }

        vmFireStore.posts.observe(viewLifecycleOwner) { posts ->
            adapter = PostAdapter(posts, postVm, onPostClicked = { post ->
                navigateToProfile(post.userid)
            }, lifecycleOwner = viewLifecycleOwner)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
            adapter.updatePosts(posts)
        }

        postVm.dataChanged.observe(viewLifecycleOwner, Observer { dataChanged ->
            if (dataChanged) {
                adapter.notifyDataSetChanged()
            }
        })

        fetchPosts()
        showDailyChallenge()

    }

    private fun fetchPosts() {
        vmFireStore.fetchPosts()
    }

    private fun showDailyChallenge() {
        challengesVm.getDailyChallenge { latestChallenge ->
            if (latestChallenge != null) {
                binding.dailyChallengeTv.text = latestChallenge.challenge
            } else {
                Log.d("HomeFragment", "No challenges available.")
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
}