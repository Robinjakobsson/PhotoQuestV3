package com.example.photoquestv3.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R
import com.example.photoquestv3.Adapter.ChallengesRecyclerAdapter

class ChallengesFragment : Fragment() {

    companion object {
        fun newInstance() = ChallengesFragment()
    }

    //  private val viewModel: ChallengesViewModel by viewModels()

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
            Challenges("Challenge 1", false),
            Challenges("Challenge 2", true),
            Challenges("Challenge 3", false),
            Challenges("Challenge 4", true),
            Challenges("Challenge 5", false),
            Challenges("Challenge 6", true),
            Challenges("Challenge 7", false),
            Challenges("Challenge 8", true),
            Challenges("Challenge 9", false),
            Challenges("Challenge 10", true),
            Challenges("Challenge 11", true),
            Challenges("Challenge 12", true)

        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.challengesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = ChallengesRecyclerAdapter(requireContext(), challenges)
        recyclerView.adapter = adapter

        return view

    }
}