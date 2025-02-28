package com.example.photoquestv3.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.databinding.MoreOptionsPostBottonSheetFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

                                editPostTextDialog(postId)
                                postVm.updatePostAdapter()
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

    private fun editPostTextDialog(postId: String?) {
        if (postId != null) {
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection("posts").document(postId)

            documentRef.get().addOnSuccessListener { document ->
                val userId = document.getString("userid") ?: ""
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                Log.d("!!!", userId)
                Log.d("!!!", currentUserId)
                if (userId == currentUserId) {

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.edit_post))
                    val input = EditText(requireContext())
                    val currentDescription = document.getString("description") ?: ""
                    input.setText(currentDescription)
                    builder.setView(input)

                    builder.setPositiveButton(getString(R.string.update)) { dialog, _ ->
                        val newText = input.text.toString().trim()
                        if (newText.isNotEmpty()) {
                            postVm.updatePostText(postId, newText, onSuccess = {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.post_updated),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }, onFailure = {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.failed_update_post),
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.enter_new_text),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        dialog.dismiss()
                    }

                    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.edit_others_description),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}