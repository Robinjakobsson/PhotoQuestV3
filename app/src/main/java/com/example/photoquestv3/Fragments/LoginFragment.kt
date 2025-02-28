package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.photoquestv3.Repositories.ChallengesRepository
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

        //used by facebook
        binding.fbLoginButton.setPermissions("email")
        binding.fbLoginButton.setOnClickListener { signInWithFb() }

//returns to HomeActivity if back button pressed
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

    //creates a launcher for google auth
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

    //open fb login page and checks for successful authentication
    private fun signInWithFb() {
        binding.fbLoginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.d("FBLogin", "Facebook login cancelled by user.")
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.fb_login_cancelled),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException) {
                    Log.e("FBLogin", "Error during Facebook login: ${error.message}", error)
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.fb_login_error, error.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }
            })
    }

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
                            val challenges = ChallengesRepository()

                            val email = currentUser.email!!
                            val name = currentUser.displayName!!
                            val username = currentUser.displayName!!.lowercase()
                            val imageUri = currentUser.photoUrl!!
                            auth.createGoogleOrFacebookAccount(
                                email,
                                "why do we have it?",
                                name,
                                username,
                                imageUri,
                                "",
                                { Log.d("GoogleDebug", "Account created successfully!") },
                                { exception ->
                                    Log.d(
                                        "GoogleDebug",
                                        "The account could not be created: ${exception.message}"
                                    )
                                }
                            )
                            Handler(Looper.getMainLooper()).postDelayed({
                                challenges.addChallengesToNewUser()
                                Toast.makeText(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                                startFeedActivity()
                            }, 1000)
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

    //gets token from fb and start methods that create new user and challenges
    private fun handleFacebookAccessToken(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener {
                Log.d("FacebookDebug", "Facebook authentication failed: ${it.message}")
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.fb_auth_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnSuccessListener { result ->
                val email = result.user?.email
                Log.d("FacebookDebug", "Facebook authentication successful. Email: $email")
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.fb_auth_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnSuccessListener { result ->
                val email = result.user?.email
                Log.d(
                    "FacebookDebug",
                    "Facebook authentication successful. Email: $email"
                )
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.fb_auth_success),
                    Toast.LENGTH_SHORT
                ).show()
                getDataFromFb()
                if (firebaseAuth.currentUser != null) {
                    val challenges = ChallengesRepository()
                    Handler(Looper.getMainLooper()).postDelayed({
                        challenges.addChallengesToNewUser()
                        Toast.makeText(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT).show()
                        startFeedActivity()
                    }, 1000)
                } else {
                    Log.d("GoogleFacebookSignIn", "User is null")
                }
            }
    }

    //part of fb auth
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    //method for signing in with email and password
    private fun signIn() {
        val email = binding.loginEmail.text.toString().trim()
        val password = binding.loginPassword.text.toString().trim()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.all_fields_required),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        } else {
            auth.signIn(email, password, onSuccess = {
                binding.progressBarLogin.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.welcome_user, email),
                    Toast.LENGTH_SHORT
                ).show()
                startFeedActivity()
            }, onFailure = {
                binding.progressBarLogin.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.login_failed),
                    Toast.LENGTH_SHORT
                ).show()
            })
        }
    }

    private fun forgotPassword() {
        val email = binding.loginEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            auth.forgotPassword(email,
                onSuccess = {
                    Toast.makeText(context, getString(R.string.mail_sent), Toast.LENGTH_SHORT).show()
                },
                onFailure = { exception ->
                    Toast.makeText(
                        context,
                        getString(R.string.oops_error, exception.message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        } else {
            Toast.makeText(
                context,
                getString(R.string.enter_valid_email),
                Toast.LENGTH_SHORT
            ).show()
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

    //Creates user for facebook auth
    private fun getDataFromFb() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val usersRef = db.collection("users")
            usersRef.document(currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        return@addOnSuccessListener
                    } else {
                        val request =
                            GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { jsonObject, response ->
                                try {
                                    val email = jsonObject!!.getString("email")
                                    val name = jsonObject.getString("name")
                                    val username = jsonObject.getString("name")
                                    val profilePicUrl =
                                        jsonObject?.getJSONObject("picture")?.getJSONObject("data")
                                            ?.getString("url")
                                    val profilePicUri = Uri.parse(profilePicUrl)
                                    auth.createGoogleOrFacebookAccount(
                                        email,
                                        "why do we have it?",
                                        name,
                                        username,
                                        profilePicUri,
                                        "",
                                        { Log.d("FacebookDebug", "Account created successfully!") },
                                        { exception ->
                                            Log.d(
                                                "FacebookDebug",
                                                "The account could not be created: ${exception.message}"
                                            )
                                        })
                                } catch (e: Exception) {
                                    Log.d("FacebookDebug", "$e")
                                }
                            }

                        val parameters = Bundle()
                        parameters.putString("fields", "email,name,picture.type(large)")
                        request.parameters = parameters
                        request.executeAsync()
                    }
                }

        }
    }
}