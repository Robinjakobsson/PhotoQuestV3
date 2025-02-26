package com.example.photoquestv3.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.StorageViewModel
import com.example.photoquestv3.ViewModel.UserViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var authVm = AuthViewModel()
    private var userVm = UserViewModel()
    private var storageVm = StorageViewModel()

    private var selectedImageUri: Uri? = null
    private val pickImgFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.imgUpdateProfileImage.setImageURI(uri)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authVm = ViewModelProvider(this)[AuthViewModel::class.java]
        userVm = ViewModelProvider(this)[UserViewModel::class.java]
        storageVm = ViewModelProvider(this)[StorageViewModel::class.java]

        binding.imgUpdateProfileImage.setOnClickListener {
            pickImgFromGallery.launch("image/*")
        }

        binding.deleteAccount.setOnClickListener {
            showPopup()
        }

        binding.buttonLogout.setOnClickListener{
            returnHomeActivity()
        }

        binding.buttonUpdate.setOnClickListener {

            updateProfileData()


        }

    }

    private fun showPopup() {
        val builder = context?.let { AlertDialog.Builder(it) }
        builder!!.setTitle("Yo!")
            .setMessage("Do you want to delete account?")
            .setPositiveButton("Yes") { _, _ ->
                userVm.deleteUserAccount(onSuccess = {
                    returnHomeActivity()
                    Toast.makeText(requireContext(), "Account successfully deleted", Toast.LENGTH_SHORT).show()
                }, onFailure = {
                    Toast.makeText(requireContext(), "Error deleting account", Toast.LENGTH_SHORT).show()
                })
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun returnHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun updateProfileData() {

        val name = binding.etUpdateNameInput.text.toString()
        val username = binding.etUpdateUserNameInput.text.toString()
        val bio = binding.etBioInput.text.toString()
        val profileImg = binding.imgUpdateProfileImage

        if (name.isNotEmpty() && username.isNotEmpty() && bio.isNotEmpty()) {
            Toast.makeText(requireContext(), "Updated!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
        }


    }

//    private fun register() {
//        val email = binding?.emailEt?.text.toString()
//        val password = binding?.passwordEt?.text.toString()
//        val name = binding?.nameEt?.text.toString()
//        val bio = binding?.biographyEt?.text.toString()
//        val username = binding?.usernameEt?.text.toString()
//        val imageUri = selectedImageUri
//
//        binding?.progressBar?.visibility = View.VISIBLE
//
//        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || bio.isEmpty() || username.isEmpty()) {
//            Toast.makeText(requireContext(),"All fields are required!",Toast.LENGTH_SHORT).show()
//            return
//        }
//        if (imageUri == null) {
//            Toast.makeText(requireContext(),"Please select a picture!",Toast.LENGTH_SHORT).show()
//            return
//        }else {
//            auth.createAccount(email,password,name,username,imageUri,bio, onSuccess = {
//
//
//                Handler(Looper.getMainLooper()).postDelayed({
//                    challenges.addChallengesToNewUser()
//
//                    Toast.makeText(requireContext(), "Welcome $username", Toast.LENGTH_SHORT).show()
//                    binding?.progressBar?.visibility = View.GONE
//                    startFeedActivity()
//                }, 1000)
//
//            }, onFailure = {
//                binding?.progressBar?.visibility = View.GONE
//                Toast.makeText(requireContext(),"Account not created..",Toast.LENGTH_SHORT).show()
//            })
//        }
//    }


}
