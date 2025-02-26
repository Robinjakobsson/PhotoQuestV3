package com.example.photoquestv3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.FollowerAdapter
import com.example.photoquestv3.Adapter.LikesAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.Repositories.FireStoreRepository
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.databinding.FragmentFollowerBinding
import com.example.photoquestv3.databinding.FragmentProfileBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FollowerFragment() : BottomSheetDialogFragment() {

    private lateinit var fireStoreVm : FireStoreViewModel
    private var binding: FragmentFollowerBinding? = null
    private lateinit var adapter : FollowerAdapter
    private lateinit var auth : AuthViewModel
    private var userid : String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = FragmentFollowerBinding.inflate(inflater,container,false)

            userid = arguments?.getString("userid")

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireStoreVm = ViewModelProvider(requireActivity()).get(FireStoreViewModel::class.java)
        auth = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding?.followerRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        adapter = FollowerAdapter(mutableListOf())
        binding?.followerRecyclerView?.adapter = adapter

        if (userid != null) {
            binding?.progressBar?.visibility = View.VISIBLE

            fireStoreVm.getFollowers(userid!!, onSuccess = {
                binding?.progressBar?.visibility = View.GONE
            }, onFailure = {
                binding?.progressBar?.visibility = View.GONE
            } )
        }
        fireStoreVm.followers.observe(viewLifecycleOwner, Observer { users ->
            adapter.updateData(users)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}