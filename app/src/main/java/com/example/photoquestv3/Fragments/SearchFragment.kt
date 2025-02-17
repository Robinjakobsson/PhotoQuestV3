package com.example.photoquestv3.Views.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.Adapter.SearchResultsAdapter
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.databinding.FragmentRegisterBinding
import com.example.photoquestv3.databinding.FragmentSearchBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null
    val db = FirebaseFirestore.getInstance()
    private lateinit var fireStoreVm : FireStoreViewModel
    private lateinit var adapter : SearchResultsAdapter


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
        fireStoreVm = ViewModelProvider(this)[FireStoreViewModel::class.java]

        binding?.searchResultsRecyclerView?.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = SearchResultsAdapter(requireContext(), mutableListOf())
        binding?.searchResultsRecyclerView?.adapter = adapter


        fireStoreVm.getUsers("").observe(viewLifecycleOwner, {users ->
            adapter.updateData(users)
        })


        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //showSearchResults()
                Log.d("!!!", "Användaren söker efter: $query")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    fireStoreVm.getUsers(it).observe(viewLifecycleOwner, {users ->
                        adapter.updateData(users)
                    })
                }
                return true
            }
        })
    }
}

