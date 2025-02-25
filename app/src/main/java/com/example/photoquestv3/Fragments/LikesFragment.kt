package com.example.photoquestv3.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.LikesAdapter
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.databinding.FragmentLikesBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LikesFragment(val postId: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentLikesBinding? = null
    private val binding get() = _binding!!

    private lateinit var postVm: PostViewModel

    lateinit var adapter: LikesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postVm = ViewModelProvider(this)[PostViewModel::class.java]

        binding.likesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LikesAdapter(mutableListOf())
        binding.likesRecyclerView.adapter = adapter

        postVm.fetchFriendList(postId).observe(viewLifecycleOwner) { friends ->
            Log.d("!!!", "Observed friends: ${friends.size}")
            adapter.updateList(friends)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}