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
            Challenges("Photograph a sunset", "2025-02-11", true),
            Challenges("Take a picture from an unusual angle","2025-02-10", true),
            Challenges("Photograph something that symbolizes happiness","2025-02-09", true),
            Challenges("Take a photo of something in the rain","2025-02-08", false),
            Challenges("Photograph something in nature that is reflecting", "2025-02-07",false),
            Challenges("Take a black-and-white photo", "2025-02-06",true),
            Challenges("Take a picture of something in motion","2025-02-05", false),
            Challenges("Photograph something old or antique", "2025-02-04",true),
            Challenges("Take a portrait of someone", "2025-02-03",true),
            Challenges("Take a picture of a reflection", "2025-02-02",false)

        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.challengesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = ChallengesRecyclerAdapter(requireContext(), challenges)
        recyclerView.adapter = adapter

        return view

    }
}