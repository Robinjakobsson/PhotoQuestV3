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
import com.example.photoquestv3.databinding.FragmentHomeBinding
import com.example.photoquestv3.databinding.FragmentRegisterBinding


class HomeFragment : Fragment() {
    private var binding : FragmentHomeBinding? = null
    private lateinit var challengesVm : ChallengesViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]

        mockData()

        showDailyChallenge()


    }
    private fun mockData() {

        val postList = Post.mockData()
        val postAdapter = PostAdapter(postList)

        binding?.recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerView?.adapter = postAdapter


    }

    private fun showDailyChallenge() {
        challengesVm.getDailyChallenge { latestChallenge ->
            if (latestChallenge != null) {
                binding?.dailyChallengeTv?.text = latestChallenge.challenge
            } else {
                Log.d("HomeFragment","No challenges.")
            }
        }
    }
}