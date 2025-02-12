package com.example.photoquestv3.Views.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Adapter.SearchResultsAdapter
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.databinding.FragmentRegisterBinding
import com.example.photoquestv3.databinding.FragmentSearchBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showSearchResults() // Här får du sökfrågan när användaren är klar
                // Gör något med frågan, t.ex. starta en sökning
                Log.d("!!!", "Användaren söker efter: $query")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //TODO I don't use this method, onQueryTextSubmit() doesn't want to work without this one, fix later
                return false
            }
        })
    }

    fun showSearchResults() { //TODO make search case insensitive, new variable with lowercase should be added to firebase
        val searchedUserNamePrefix = binding!!.searchView.query.toString()
        val searchEnd = searchedUserNamePrefix + '\uf8ff'
        if (searchedUserNamePrefix.isNotEmpty()) {
            var matchingUsers = mutableListOf<User>()
            val collectionRef = db.collection("users")



            val query = collectionRef.whereGreaterThanOrEqualTo("usernamesearch", searchedUserNamePrefix)
                .whereLessThanOrEqualTo("usernamesearch", searchEnd)
            query.get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            Log.d("!!!", "Hittade dokument")
                            val foundMatchingUser = document.toObject(User::class.java)
                            matchingUsers.add(foundMatchingUser)
                            Log.d("!!!", matchingUsers.toString())
                            startRecycleViewWithResults(matchingUsers)
                        }
                    } else {
                        Log.d("!!!", "Inga dokument hittades")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("!!!", "Fel vid hämtning av dokument: $exception")
                }
        }
    }

    fun startRecycleViewWithResults(matchingUsers: MutableList<User>) {

        binding?.searchResultsRecyclerView?.layoutManager =
            GridLayoutManager(requireContext(), 3) //определяет, что элементы идут по порядку
        val adapter = SearchResultsAdapter(
            requireContext(),
            matchingUsers
        )//создает объект класса адаптер(нашего конкретно, засылает в него список)
        binding?.searchResultsRecyclerView?.adapter = adapter
    }
} //вставляет адаптер в наш ресайкл

