package com.example.photoquestv3.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Adapter.CommentAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.CommentViewModel
import com.example.photoquestv3.databinding.FragmentCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CommentFragment(private val postId: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmComment: CommentViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmComment = ViewModelProvider(this)[CommentViewModel::class.java]
        vmComment.startListeningToComments(postId)
        vmComment.comments.observe(viewLifecycleOwner) { comments ->
            binding.commentSection.adapter = CommentAdapter(comments)
            binding.commentSection.scrollToPosition(comments.size - 1)
            binding.commentSection.adapter?.notifyDataSetChanged()
        }

        binding.editTextComment.setOnClickListener {

            addComment()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addComment() {
        val input = binding.editTextComment.text.toString()
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
        } else {
           vmComment.addComment(postId, input, onSuccess = {
               binding.editTextComment.text.clear()
           }, onFailure = {
               Toast.makeText(requireContext(), "Failed to add comment", Toast.LENGTH_SHORT).show()
           })


        }
    }

}