package com.example.photoquestv3.Views.Fragments

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.ChallengeObjects
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.Views.ChallengesRecyclerAdapter

class ChallengesFragment : Fragment() {

    companion object {
        fun newInstance() = ChallengesFragment()
    }

    private val viewModel: ChallengesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_challenges, container, false)

        val challenges = mutableListOf(
            Challenges("Challenge 1", true),
            Challenges("Challenge 2", false)
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.challengesRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = ChallengesRecyclerAdapter(requireContext(), challenges)

        recyclerView.adapter = adapter

        return view

    }
}