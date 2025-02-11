package com.example.photoquestv3.Fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private var binding : FragmentRegisterBinding? = null
    private lateinit var auth : AuthViewModel
    private var selectedImageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]

        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) {uri: Uri? ->
                if (uri != null) {
                    selectedImageUri = uri
                    binding?.imagePicker?.setImageURI(selectedImageUri)
                }
            }

        binding?.imagePicker?.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding?.registerButton?.setOnClickListener { register() }

    }

    private fun register() {
        val email = binding?.emailEt?.text.toString()
        val password = binding?.passwordEt?.text.toString()
        val name = binding?.nameEt?.text.toString()
        val bio = binding?.biographyEt?.text.toString()
        val username = binding?.usernameEt?.text.toString()

        val imageUri = selectedImageUri

        binding?.progressBar?.visibility = View.VISIBLE

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || bio.isEmpty() || username.isEmpty()) {
            Toast.makeText(requireContext(),"All fields are required!",Toast.LENGTH_SHORT).show()
            return
        }
        if (imageUri == null) {
            Toast.makeText(requireContext(),"Please select a picture!",Toast.LENGTH_SHORT).show()
            return
        }else {
            auth.createAccount(email,password,name,username,imageUri,bio, onSuccess = {
                Toast.makeText(requireContext(),"Welcome $username",Toast.LENGTH_SHORT).show()
                binding?.progressBar?.visibility = View.GONE
            }, onFailure = {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(),"Account not created..",Toast.LENGTH_SHORT).show()
            })
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
    }

}