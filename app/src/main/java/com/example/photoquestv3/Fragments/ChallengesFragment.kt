package com.example.photoquestv3.Fragments


import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R
import com.example.photoquestv3.Adapter.ChallengesRecyclerAdapter
import com.example.photoquestv3.Models.ChallengeObjects
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class ChallengesFragment : Fragment() {

    companion object {
        fun newInstance() = ChallengesFragment()
    }

    lateinit var adapter: ChallengesRecyclerAdapter

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
             Challenges("Photograph a sunset", "2025-02-15",false),
             Challenges("Take a picture from an unusual angle","2025-02-14", true),
             Challenges("Photograph something that symbolizes happiness","2025-02-13", false),
             Challenges("Take a photo of something in the rain", "2025-02-12",false),
             Challenges("Photograph something in nature that is reflecting","2025-02-11", false),
             Challenges("Take a black-and-white photo","2025-02-10", false),
             Challenges("Take a picture of something in motion", "2025-02-09",false),
             Challenges("Photograph something old or antique", "2025-02-08",true),
             Challenges("Take a portrait of someone","2025-02-07", false),
             Challenges("Take a picture of a reflection", "2025-02-06",false),
             Challenges("Photograph a flower up close", "2025-02-05",false),
             Challenges("Take a picture of your own shadow", "2025-02-04",false),
             Challenges("Photograph something red", "2025-02-11",false),
             Challenges("Photograph a city skyline at night", "2025-02-03",false),
             Challenges("Take a picture with a reflective object", "2025-02-02",false),
             Challenges("Photograph an animal in its natural habitat","2025-02-01", false)
         )
        val recyclerView = view.findViewById<RecyclerView>(R.id.challengesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ChallengesRecyclerAdapter(requireContext(), challenges)
        recyclerView.adapter = adapter

        return view

    }

}