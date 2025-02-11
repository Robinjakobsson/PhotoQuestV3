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
            Challenges("Photograph a sunset", false),
            Challenges("Take a picture from an unusual angle", true),
            Challenges("Photograph something that symbolizes happiness", true),
            Challenges("Take a photo of something in the rain", false),
            Challenges("Photograph something in nature that is reflecting", false),
            Challenges("Take a black-and-white photo", true),
            Challenges("Take a picture of something in motion", false),
            Challenges("Photograph something old or antique", true),
            Challenges("Take a portrait of someone", true),
            Challenges("Take a picture of a reflection", false)

        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.challengesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = ChallengesRecyclerAdapter(requireContext(), challenges)
        recyclerView.adapter = adapter

        return view

    }
}