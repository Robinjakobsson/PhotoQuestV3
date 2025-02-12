package com.example.photoquestv3.Fragments


import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R
import com.example.photoquestv3.Adapter.ChallengesRecyclerAdapter
import com.example.photoquestv3.Models.ChallengeObjects
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.databinding.FragmentChallengesBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class ChallengesFragment : Fragment() {

    lateinit var binding: FragmentChallengesBinding
    val listOfChallenges = mutableListOf<Challenges>()
    lateinit var adapter: ChallengesRecyclerAdapter
    lateinit var vmChallenges: ChallengesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChallengesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleViewSetup()

    }

    private fun recycleViewSetup() {
        binding.challengesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChallengesRecyclerAdapter(requireContext(), listOfChallenges)
        binding.challengesRecyclerView.adapter = adapter

        vmChallenges = ViewModelProvider(this)[ChallengesViewModel::class.java]

        vmChallenges.challenges.observe(viewLifecycleOwner) { challenges ->
            adapter.updateChallenges(challenges)
            binding.challengesRecyclerView.scrollToPosition(challenges.size-1)

        }
        vmChallenges.getChallengesFromDatabase()
    }


}