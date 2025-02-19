package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.net.Uri
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
import com.example.photoquestv3.Fragments.RegisterFragment
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.appevents.UserDataStore.EMAIL
import com.facebook.login.LoginResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.auth


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: AuthViewModel
    lateinit var callbackManager: CallbackManager
lateinit var firebaseAuth : FirebaseAuth

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
        callbackManager = CallbackManager.Factory.create();
        firebaseAuth = Firebase.auth
        binding.fbLoginButton.setPermissions("email")
        binding.fbLoginButton.setOnClickListener {
            signInWithFb()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as? HomeActivity)?.getButtonsBack()
                    parentFragmentManager.beginTransaction().remove(this@LoginFragment).commit()
                }
            })


        binding.buttonLogin.setOnClickListener { signIn() }
        binding.forgotPassword.setOnClickListener { forgotPassword() }


    }

    private fun signInWithFb() {
        binding.fbLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {

                }

                override fun onError(error: FacebookException) {

                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result!!.accessToken)
                    getDataFromFb()
                    //  auth.createAccount()
                }

            })
    }

    private fun getDataFromFb() {
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            try {
                val email = `object`!!.getString("email")
                val name = `object`!!.getString("name")

                // Här kan du lägga till ytterligare logik för att hämta eller generera andra nödvändiga fält
                val username = `object`!!.getString("name") // Exempel på hur man kan generera ett användarnamn
               //val imageUri = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fpctidningen.se%2Finternet%2Ffacebook%2Fsnabbstadning-pa-facebook&psig=AOvVaw0iM7864gvcCc_xMLdahXVE&ust=1740057600508000&source=images&cd=vfe&opi=89978449&ved=0CBYQjRxqFwoTCJDNl-vpz4sDFQAAAAAdAAAAABAE"
                val biography = "Bio från Facebook-användare"
                val imageUri = Uri.parse("android.resource://com.example.photoquestv3/${R.drawable.facebook}")
                auth.createAccount(email, "why do we have it?", name, username, imageUri, biography, {
                    Log.d("!!!","Kontot skapades framgångsrikt!")
                }, { exception ->
                    Log.d("!!!","Det gick inte att skapa kontot: ${exception.message}")
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "email,name,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookAccessToken(accessToken : AccessToken) {
//get credential
    val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
firebaseAuth.signInWithCredential(credential)
    .addOnFailureListener {
        Toast.makeText(requireActivity(), "Mess",Toast.LENGTH_SHORT).show()
    }
    .addOnSuccessListener { result ->
        val email = result.user!!.email
        Toast.makeText(requireActivity(), "Yay",Toast.LENGTH_SHORT).show()
    }
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun signIn() {
        val email = binding.loginEmail.text.toString().trim()
        val password = binding.loginPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
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

    private fun forgotPassword() {
        val email = binding.loginEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            auth.forgotPassword(email,
                onSuccess = { Toast.makeText(context, "Mail sent!", Toast.LENGTH_SHORT).show() },
                onFailure = { exception ->
                    Toast.makeText(context, "Ooops: ${exception.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        } else {
            Toast.makeText(context, "Enter a valid email-address", Toast.LENGTH_SHORT).show()
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
