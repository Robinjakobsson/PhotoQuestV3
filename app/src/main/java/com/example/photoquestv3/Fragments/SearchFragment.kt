package com.example.photoquestv3.Views.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photoquestv3.Adapter.SearchResultsAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private var binding: FragmentSearchBinding? = null
    private lateinit var fireStoreVm: FireStoreViewModel
    private lateinit var adapter: SearchResultsAdapter


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

        adapter = SearchResultsAdapter(requireContext(), mutableListOf(), onUserClicked = { user ->
            navigateToProfile(user.uid)
        })
        binding?.searchResultsRecyclerView?.adapter = adapter



        fireStoreVm.getUsers("").observe(viewLifecycleOwner, { users ->
            adapter.updateData(users)
        })


        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    fireStoreVm.getUsers(it).observe(viewLifecycleOwner, { users ->
                        adapter.updateData(users)
                    })
                }
                return true
            }
        })
    }

    fun navigateToProfile(uid: String) {
        val bundle = Bundle()

        bundle.putString("uid", uid)

        val profileFragment = ProfileFragment()
        profileFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, profileFragment)
            .addToBackStack(null)
            .commit()
    }
}

