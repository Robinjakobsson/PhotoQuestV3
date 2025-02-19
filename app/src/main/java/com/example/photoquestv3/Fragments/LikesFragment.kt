package com.example.photoquestv3.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.ChallengesRecyclerAdapter
import com.example.photoquestv3.Adapter.LikesAdapter
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.ViewModel.CommentViewModel
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.databinding.FragmentChallengesBinding
import com.example.photoquestv3.databinding.FragmentLikesBinding
import com.example.photoquestv3.databinding.MoreOptionsPostBottonSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LikesFragment(val postId: String) : BottomSheetDialogFragment() {

    private var binding: FragmentLikesBinding? = null
    var listOfFriends = mutableListOf<User>()
    lateinit var postVm: PostViewModel

    lateinit var adapter: LikesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikesBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postVm = ViewModelProvider(this)[PostViewModel::class.java]

        binding?.likesRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        adapter = LikesAdapter(requireContext(), listOfFriends)
        binding?.likesRecyclerView?.adapter = adapter

        postVm.listOfFriends.observe(viewLifecycleOwner) { friends ->
            Log.d("!!!", "Observed friends: ${friends.size}")
            adapter.updateList(friends)

        }

        postVm.fetchFriendsLiked(postId)
    }

}