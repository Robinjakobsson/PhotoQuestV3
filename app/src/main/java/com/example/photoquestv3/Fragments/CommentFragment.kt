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
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.UserViewModel
import com.example.photoquestv3.Views.Fragments.ProfileFragment
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
            commentAdapter.currentUserData = user
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

        binding.commentSendButton.setOnClickListener{
            addComment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Adds a comment to the database by calling the addComment() function in the CommentViewModel.
     */
    private fun addComment() {
        val input = binding.editTextComment.text.toString()
        if (input.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.please_enter_comment), Toast.LENGTH_SHORT).show()
        } else {
            vmComment.addComment(postId, input, onSuccess = {
                binding.editTextComment.text.clear()
            }, onFailure = {
                Toast.makeText(requireContext(), getString(R.string.failed_to_add_comment), Toast.LENGTH_SHORT).show()
            })
        }
    }

    /**
     * Edits a comment in the database by calling the updateComment() function in the CommentViewModel.
     */
    private fun editCommentDialog(comment: Comment) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.edit_comment))

        val input = EditText(requireContext())
        input.setText(comment.comment)
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.update)) { dialog, _ ->
            val newText = input.text.toString().trim()
            if (newText.isNotEmpty()) {
                vmComment.updateComment(comment.commentId, newText, onSuccess = {
                    Toast.makeText(requireContext(), getString(R.string.comment_updated), Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(requireContext(), getString(R.string.failed_to_update_comment), Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_enter_comment), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }



    /**
     * Sets up the RecyclerView for displaying comments.
     */
    private fun recycleViewSetup()  {
        binding.commentSection.layoutManager = LinearLayoutManager(requireContext())
        commentAdapter = CommentAdapter(emptyList(), currentUserData = vmUser.userData.value, onCommentClicked = {
                comment -> editCommentDialog(comment)
        }, onUserClicked = { user ->
            navigateToProfile(user.userId)
        })
        binding.commentSection.adapter = commentAdapter
    }

    /**
     * Deletes a comment from the database by calling the deleteComment() function in the CommentViewModel.
     */

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
                        Toast.makeText(requireContext(), getString(R.string.comment_deleted), Toast.LENGTH_SHORT).show()
                    }, onFailure = {
                        Toast.makeText(requireContext(), getString(R.string.failed_to_delete_comment), Toast.LENGTH_SHORT).show()
                    })
                } else {
                    Toast.makeText(requireContext(), getString(R.string.only_delete_own_comments), Toast.LENGTH_SHORT).show()
                    commentAdapter.notifyItemChanged(position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.commentSection)
    }

    private fun navigateToProfile(uid: String) {
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

