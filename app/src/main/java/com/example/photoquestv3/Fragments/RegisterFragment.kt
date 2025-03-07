package com.example.photoquestv3.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.R
import com.example.photoquestv3.Repositories.ChallengesRepository
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.FeedActivity
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null
    private lateinit var auth: AuthViewModel
    private var selectedImageUri: Uri? = null

    private val challenges = ChallengesRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as? HomeActivity)?.getButtonsBack()
                    parentFragmentManager.beginTransaction().remove(this@RegisterFragment).commit()
                }
            })
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
            Toast.makeText(requireContext(), getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show()
            return
        }
        if (imageUri == null) {
            Toast.makeText(requireContext(), getString(R.string.please_select_picture), Toast.LENGTH_SHORT).show()
            return
        } else {
            auth.createAccount(email, password, name, username, imageUri, bio, onSuccess = {

                Handler(Looper.getMainLooper()).postDelayed({
                    challenges.addChallengesToNewUser()

                    Toast.makeText(requireContext(), getString(R.string.welcome_user, username), Toast.LENGTH_SHORT).show()
                    binding?.progressBar?.visibility = View.GONE
                    startFeedActivity()
                }, 1000)

            }, onFailure = {
                binding?.progressBar?.visibility = View.GONE
                Toast.makeText(requireContext(), getString(R.string.account_not_created), Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun startFeedActivity() {
        val intent = Intent(requireActivity(), FeedActivity::class.java)
        requireActivity().startActivity(intent)
    }
}