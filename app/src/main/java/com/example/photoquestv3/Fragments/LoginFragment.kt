package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.FeedActivity
import com.example.photoquestv3.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.HomeActivity


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as? HomeActivity)?.getButtonsBack()
                    parentFragmentManager.beginTransaction().remove(this@LoginFragment).commit()
                }
            })
        binding.buttonLogin.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val email = binding.loginEmail.text.toString().trim()
        val password = binding.loginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(),"All fields are required!",Toast.LENGTH_SHORT).show()
            return

        } else {

            binding.progressBarLogin.visibility = View.VISIBLE

            auth.signIn(email, password, onSuccess = {
                binding.progressBarLogin.visibility = View.GONE
                Toast.makeText(requireContext(), "Welcome! $email", Toast.LENGTH_SHORT).show()
                startFeedActivity()
            }, onFailure = {
                binding.progressBarLogin.visibility = View.GONE
                Toast.makeText(requireContext(), "Login failed..", Toast.LENGTH_SHORT).show()
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun startFeedActivity() {
        val intent = Intent(requireActivity(), FeedActivity::class.java)
        requireActivity().startActivity(intent)

    }
}
