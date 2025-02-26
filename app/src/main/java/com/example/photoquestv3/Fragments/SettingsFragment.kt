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

//            updateProfilePic()
//            updateName()
            updateUserName()
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

    private fun updateProfilePic() {
        selectedImageUri?.let { uri ->

            storageVm.uploadProfileImage(uri, onSuccess = { downloadUrl ->
                try {
                    val currentUser = authVm.getCurrentUserUid()
                    userVm.updateUserField(currentUser, "imageUrl", downloadUrl,
                        onSuccess = {
                            Toast.makeText(
                                requireContext(),
                                "Profile picture updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }, onFailure = { e ->
                            Toast.makeText(
                                requireContext(),
                                "Failed to update image: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT)
                        .show()
                }
            }, onFailure = {
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })
        } ?: run { Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show() }
    }

    private fun updateName() {

        val newName = binding.etUpdateNameInput.text.toString()
        if (newName.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = authVm.getCurrentUserUid()
            userVm.updateUserField(currentUser, "name", newName,
                onSuccess = {
                    Toast.makeText(requireContext(), "Name is updated", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failed to update name", Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun updateUserName() {

        val newUserName = binding.etUpdateUserNameInput.text.toString()
        if (newUserName.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = authVm.getCurrentUserUid()
            userVm.updateUserField(currentUser, "name", newUserName,
                onSuccess = {
                    Toast.makeText(requireContext(), "Name is updated", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failed to update name", Toast.LENGTH_SHORT).show()
                })
        }
    }

    private fun updateBio() {

        val newBio = binding.etBioInput.text.toString()
        if (newBio.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = authVm.getCurrentUserUid()
            userVm.updateUserField(currentUser, "name", newBio,
                onSuccess = {
                    Toast.makeText(requireContext(), "Name is updated", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failed to update name", Toast.LENGTH_SHORT).show()
                })
        }
    }
}
