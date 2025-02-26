package com.example.photoquestv3.Fragments

import android.os.Bundle
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
import com.example.photoquestv3.ViewModel.CommentViewModel
import com.example.photoquestv3.databinding.FragmentCommentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.photoquestv3.ViewModel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class CommentFragment(private val postId: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var vmComment: CommentViewModel
    private lateinit var vmUser: UserViewModel
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
        vmUser = ViewModelProvider(this)[UserViewModel::class.java]

        vmComment.startListeningToComments(postId)
        recycleViewSetup()
        swipeToDeleteComment()
        vmComment.comments.observe(viewLifecycleOwner) { comments ->
            commentAdapter.updateComments(comments)

            if (comments.isNotEmpty()) {
                binding.commentSection.scrollToPosition(comments.size - 1)
            }
        }

        vmUser.userData.observe(viewLifecycleOwner) { user ->
            commentAdapter.currentUserProfileUrl = user?.imageUrl
            commentAdapter.notifyDataSetChanged()
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
    private fun recycleViewSetup()  {
        binding.commentSection.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter = CommentAdapter(emptyList(), currentUserProfileUrl = null) { comment -> editCommentDialog(comment) }
        binding.commentSection.adapter = commentAdapter
    }
    private fun swipeToDeleteComment() {

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition

                val commentToDelete = commentAdapter.getCommentAt(position)
                val currentUser = Firebase.auth.currentUser?.uid ?: "No user here"

                if (commentToDelete.userId == currentUser) {
                    vmComment.deleteComment(commentToDelete.commentId, onSuccess = {
                        Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
                    },onFailure = {
                        Toast.makeText(requireContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show()
                    })
                } else {
                    Toast.makeText(requireContext(), "You can only delete your own comments", Toast.LENGTH_SHORT).show()
                    commentAdapter.notifyItemChanged(position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.commentSection)
    }

}

