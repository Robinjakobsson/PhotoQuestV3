package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.databinding.FragmentHomeBinding
import com.example.photoquestv3.databinding.FragmentRegisterBinding


class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var challengesVm : ChallengesViewModel
    private lateinit var vmFireStore : FireStoreViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]
        vmFireStore = ViewModelProvider(this)[FireStoreViewModel::class.java]

        fetchPosts()
        showDailyChallenge()


    }
    private fun fetchPosts() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        vmFireStore.posts.observe(viewLifecycleOwner) { posts ->
            val adapter = PostAdapter(posts)
            binding.recyclerView.adapter = adapter
        }
        vmFireStore.fetchPosts()
    }

    private fun showDailyChallenge() {
        challengesVm.getDailyChallenge { latestChallenge ->
            if (latestChallenge != null) {
                binding.dailyChallengeTv.text = latestChallenge.challenge
            } else {
                Log.d("HomeFragment","No challenges.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}