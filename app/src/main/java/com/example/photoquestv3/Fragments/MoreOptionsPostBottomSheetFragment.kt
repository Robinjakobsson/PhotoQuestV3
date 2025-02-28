package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.databinding.MoreOptionsPostBottonSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreOptionsPostBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: MoreOptionsPostBottonSheetFragmentBinding? = null

    private lateinit var postVm: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MoreOptionsPostBottonSheetFragmentBinding.inflate(inflater, container, false)
        postVm = ViewModelProvider(requireActivity())[PostViewModel::class.java]

        postVm.toastMessage.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        postVm.itemId.observe(viewLifecycleOwner) { itemId ->

            Log.d("!!!", "my post id is: $itemId")
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = binding?.listView
        val options = listOf("Edit post", "Delete post", " ")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, options)

        if (listView != null) {
            listView.adapter = adapter
        }

        if (listView != null) {

            postVm.itemId.observe(viewLifecycleOwner, Observer { postId ->

                if (postId != null) {

                    listView.setOnItemClickListener() { _, _, position, _ ->

                        when (position) {

                            0 -> {

                            }

                            1 -> {

                                postVm.deletePost(postId)
                                postVm.updatePostAdapter()

                            }
                        }
                    }
                }
            })
        }
    }
}