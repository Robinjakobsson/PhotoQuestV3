package com.example.photoquestv3.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
    private val pickImgFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

        binding.buttonLogout.setOnClickListener {
            returnHomeActivity()
        }

        binding.buttonUpdate.setOnClickListener {
            updateUserProfile()
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

    private fun updateUserProfile() {
        val newName = binding.etUpdateNameInput.text.toString().trim()
        val newUsername = binding.etUpdateUserNameInput.text.toString().trim()
        val newBio = binding.etBioInput.text.toString().trim()

        binding.progressSettings.visibility = View.VISIBLE

        val currentUserUid = authVm.getCurrentUserUid()

        if (selectedImageUri != null) {

            storageVm.uploadProfileImage(selectedImageUri!!,
                onSuccess = { downloadUrl ->

                val existingUser = userVm.userData.value
                if (existingUser != null) {

                    val updatedUser = existingUser.copy(
                        name = if (newName.isNotEmpty()) newName else existingUser.name,
                        username = if (newUsername.isNotEmpty()) newUsername else existingUser.username,
                        biography = if (newBio.isNotEmpty()) newBio else existingUser.biography,
                        imageUrl = downloadUrl
                    )
                    userVm.updateUser(updatedUser,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        },
                        onFailure = { e ->
                            Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        }
                    )
                }
            }, onFailure = {
                Toast.makeText(requireContext(), "Failed to upload image: ${it.message}", Toast.LENGTH_SHORT).show()
                    binding.progressSettings.visibility = View.GONE
            })
        } else {
            if (newName.isNotEmpty() || newUsername.isNotEmpty() || newBio.isNotEmpty()) {

                if (newName.isNotEmpty()) {
                    userVm.updateUserField(currentUserUid, "name", newName,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        },
                        onFailure = { e ->
                            Toast.makeText(requireContext(), "Failed to update name: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        }
                    )
                }
                if (newUsername.isNotEmpty()) {
                    userVm.updateUserField(currentUserUid, "username", newUsername,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Username updated", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        },
                        onFailure = { e ->
                            Toast.makeText(requireContext(), "Failed to update username: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        }
                    )
                }
                if (newBio.isNotEmpty()) {
                    userVm.updateUserField(currentUserUid, "biography", newBio,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Biography updated", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        },
                        onFailure = { e ->
                            Toast.makeText(requireContext(), "Failed to update biography: ${e.message}", Toast.LENGTH_SHORT).show()
                            binding.progressSettings.visibility = View.GONE
                        }
                    )
                }
            } else {
                Toast.makeText(requireContext(), "Nothing to update", Toast.LENGTH_SHORT).show()
                binding.progressSettings.visibility = View.GONE
            }
        }
    }
}
