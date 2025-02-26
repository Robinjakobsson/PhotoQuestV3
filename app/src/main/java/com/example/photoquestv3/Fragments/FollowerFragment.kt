package com.example.photoquestv3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.FollowerAdapter
import com.example.photoquestv3.Adapter.LikesAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.databinding.FragmentFollowerBinding
import com.example.photoquestv3.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FollowerFragment : BottomSheetDialogFragment() {

    private lateinit var fireStoreVm : FireStoreViewModel
    private var binding: FragmentFollowerBinding? = null
    private lateinit var adapter : FollowerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireStoreVm = ViewModelProvider(requireActivity()).get(FireStoreViewModel::class.java)

        binding?.followerRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        adapter = FollowerAdapter(mutableListOf())
        binding?.followerRecyclerView?.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}