package com.example.photoquestv3.Views.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Models.Challenges
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.ViewModel.StorageViewModel
import com.example.photoquestv3.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageVm: StorageViewModel
    private var selectedImageUri : Uri? = null
    private lateinit var challengesVm: ChallengesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storageVm = ViewModelProvider(this)[StorageViewModel::class.java]
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]
        challengesVm.isChecked.observe(viewLifecycleOwner) { isChecked ->
            binding.challengeCheckbox.isChecked = isChecked }


        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {uri : Uri? ->
                if (uri != null) {
                    selectedImageUri = uri
                    binding.selectedImage.setImageURI(selectedImageUri)
                }
            }

        binding.selectImageButton.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.postButton.setOnClickListener { uploadPost() }

        showDailyChallenge()



        binding.challengeCheckbox.setOnClickListener {

            val isChecked = binding.challengeCheckbox.isChecked

            challengesVm.setChallengeCheckedState(isChecked)

            if (binding.challengeCheckbox.isChecked) {
                markChallengeDone()
            } else {
                markChallengeNotDone()
            }

        }


    }

    private fun uploadPost() {
        val description = binding.textDescription.text.toString()

        binding.progressBar.visibility = View.VISIBLE

        if (selectedImageUri != null && description.isNotBlank()) {
            storageVm.uploadPost(selectedImageUri!!,description, onSuccess = {
                Toast.makeText(requireContext(),"Successfully Uploaded Post!",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }, onFailure = {
                Toast.makeText(requireContext(),"Error",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            })
        } else {
            Toast.makeText(requireContext(),"Please Enter a text and a picture!",Toast.LENGTH_SHORT).show()
        }

    }

    private fun showDailyChallenge() {
        challengesVm.getDailyChallenge { latestChallenge ->
            if (latestChallenge != null) {
                binding.dailyChallengePostTv.text = latestChallenge.challenge
            } else {
                Log.d("HomeFragment","No challenges.")
            }
        }
    }

    private fun markChallengeDone() {
        challengesVm.markChallengeDone()
    }

    private fun markChallengeNotDone() {
        challengesVm.markChallengeNotDone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}