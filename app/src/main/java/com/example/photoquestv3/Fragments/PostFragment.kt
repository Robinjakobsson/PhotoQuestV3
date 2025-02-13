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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Fragments.ChallengesFragment
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.PostViewModel
import com.example.photoquestv3.ViewModel.StorageViewModel
import com.example.photoquestv3.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageVm: StorageViewModel
    private var selectedImageUri : Uri? = null


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


        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {uri : Uri? ->
                if (uri != null) {
                    selectedImageUri = uri
                    binding.selectedImage.setImageURI(selectedImageUri)
                }
            }

        binding.selectImageButton.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.postButton.setOnClickListener { uploadPost() }




        binding.challengeCardView.setOnClickListener{
            daily_challenge_popUp()
        }






    }

    private fun uploadPost() {
        val description = binding.textDescription.text.toString()

        if (selectedImageUri != null && description.isNotBlank()) {
            storageVm.uploadPost(selectedImageUri!!,description,)
        } else {
            Toast.makeText(requireContext(),"Please Enter a text and a picture!",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun openDeviceGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicker.launch(intent)
    }

    private fun openGallerySetup() {
        imagePicker = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                // Visa den valda bilden i ImageView
                binding.selectedImage.setImageURI(selectedImageUri)
                // Du kan även spara URI:t för att senare använda det vid skapande av ett inlägg
            }
        }
    }

    private fun daily_challenge_popUp(){
        AlertDialog.Builder(requireContext())
            .setTitle("Dagens Utmaning")
            .setMessage("Vill du se dagens utmaning?")
            .setPositiveButton("Ja"){_,_->
                replaceFragment(ChallengesFragment())

            }

            .setNegativeButton("Nej",null)
            .show()

    }


    //  fun for replacing fragment.
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()

    }

}