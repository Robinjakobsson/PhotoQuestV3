package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.FeedActivity
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: AuthViewModel
    lateinit var callbackManager: CallbackManager
    lateinit var firebaseAuth: com.google.firebase.auth.FirebaseAuth
    lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        callbackManager = CallbackManager.Factory.create()
        firebaseAuth = Firebase.auth

        // Startar Google-inloggning och hämtar idToken
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("!!!", "API problem")
            }
        }

        binding.fbLoginButton.setPermissions("email")
        binding.fbLoginButton.setOnClickListener { signInWithFb() }

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
        binding.googleSignInButton.setOnClickListener { signInWithGoogle() }
    }

    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(requireActivity(), gso)
    }

    // launches google sign in page, then awaits users token and other information
    fun signInWithGoogle() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun signInWithFb() {
        binding.fbLoginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.d("FBLogin", "Facebook login cancelled by user.")
                    Toast.makeText(requireActivity(), "Facebook login cancelled.", Toast.LENGTH_SHORT).show()
                }
                override fun onError(error: FacebookException) {
                    Log.e("FBLogin", "Error during Facebook login: ${error.message}", error)
                    Toast.makeText(requireActivity(), "Facebook login error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                    getDataFromFb()
                    startFeedActivity()
                }
            })
    }

    private fun getDataFromFb() {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { jsonObject, response ->
            try {
                val email = jsonObject!!.getString("email")
                val name = jsonObject.getString("name")
                val username = jsonObject.getString("name")
                val biography = "Bio från Facebook-användare"
                val imageUri = Uri.parse("android.resource://com.example.photoquestv3/${R.drawable.facebook}")

                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val db = FirebaseFirestore.getInstance()
                    val usersRef = db.collection("users")
                    usersRef.document(currentUser.uid).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            // Uppdatera befintlig användardata
                            usersRef.document(currentUser.uid).update(
                                mapOf(
                                    "email" to email,
                                    "name" to name,
                                    "username" to username,
                                    "profileImage" to imageUri.toString(),
                                    "biography" to biography
                                )
                            ).addOnSuccessListener {
                                Log.d("FBUpdate", "User data updated successfully")
                            }.addOnFailureListener { exception ->
                                Log.e("FBUpdate", "Failed to update user data: ${exception.message}")
                            }
                        } else {
                            // Skapa nytt konto i Firestore
                            auth.createAccount(
                                email,
                                "why do we have it?",
                                name,
                                username,
                                imageUri,
                                biography,
                                { Log.d("FacebookDebug", "Account created successfully!") },
                                { exception -> Log.d("FacebookDebug", "The account could not be created: ${exception.message}") }
                            )
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FBUpdate", "Error fetching user document: ${exception.message}")
                    }
                } else {
                    Log.d("FB", "No current user available")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "email,name,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener {
                Log.d("FacebookDebug", "Facebook authentication failed: ${it.message}")
                Toast.makeText(requireActivity(), "Facebook authentication failed.", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener { result ->
                val email = result.user?.email
                Log.d("FacebookDebug", "Facebook authentication successful. Email: $email")
                Toast.makeText(requireActivity(), "Facebook authentication successful.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(context, "Ooops: ${exception.message}", Toast.LENGTH_SHORT).show()
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

    //    Handles Google Sign-In
    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val currentUser = firebaseAuth.currentUser
                val db = FirebaseFirestore.getInstance()
                val usersRef = db.collection("users")
                usersRef.document(currentUser!!.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            return@addOnSuccessListener
                        } else {
                            auth.createAccount(
                                currentUser.email ?: "",
                                "why do we have it?",
                                currentUser.displayName ?: "",
                                currentUser.displayName ?: "",
                                Uri.parse("android.resource://com.example.photoquestv3/${R.drawable.google}"),
                                "biography",
                                { Log.d("!!!", "Kontot skapades framgångsrikt!") },
                                { exception -> Log.d("!!!", "Det gick inte att skapa kontot: ${exception.message}") }
                            )
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("!!!", "Problem: $exception")
                    }
                Log.d("!!!", "Google auth success")
                startFeedActivity()
            } else {
                Log.d("!!!", "Google auth failed")
            }
        }
    }
}