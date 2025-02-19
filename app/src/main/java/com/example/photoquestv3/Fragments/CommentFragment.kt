package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Adapter.CommentAdapter
import com.example.photoquestv3.Models.Comment
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.CommentViewModel
import com.example.photoquestv3.databinding.FragmentCommentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.appcompat.app.AlertDialog


class CommentFragment(private val postId: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmComment: CommentViewModel
    private lateinit var commentAdapter: CommentAdapter

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

        recycleViewSetup()

        vmComment.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.updateComments(comments)

            if (comments.isNotEmpty()) {
                binding.commentSection.scrollToPosition(comments.size - 1)
            }
        }

        binding.editTextComment.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addComment()
                true
            } else {
                false
            }
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

    private fun editCommentDialog(comment: Comment) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Comment")

        val input = EditText(requireContext())
        input.setText(comment.comment)
        builder.setView(input)

        builder.setPositiveButton("Update") { dialog, _ ->

            val newText = input.text.toString().trim()
            if (newText.isNotEmpty()) {
                vmComment.updateComment(comment.commentId, newText, onSuccess = {
                    Toast.makeText(requireContext(), "Comment updated", Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(requireContext(), "Failed to update comment", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(requireContext(), "Please enter a comment", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }


    private fun recycleViewSetup() {
        binding.commentSection.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter = CommentAdapter(emptyList()) { comment -> editCommentDialog(comment) }
        binding.commentSection.adapter = commentAdapter
    }

}

