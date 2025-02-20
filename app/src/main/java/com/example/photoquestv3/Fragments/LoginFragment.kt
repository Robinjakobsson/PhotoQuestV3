package com.example.photoquestv3.Views.Fragments


import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.telecom.Connection
import android.telecom.ConnectionRequest
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Fragments.RegisterFragment
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.appevents.UserDataStore.EMAIL
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.fido.fido2.Fido2ApiClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: AuthViewModel
    lateinit var callbackManager: CallbackManager
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var launcher: ActivityResultLauncher<Intent>

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


        //gets data from Google, if users data is correct and account != null, returns token that sends to firebase
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
        binding.googleSignInButton.setOnClickListener {

            signInWithGoogle()
        }

    }

    //starts google auth page, deprecated, but another code didn't work at all
    fun getClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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
                    startFeedActivity()
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
                val username =
                    `object`!!.getString("name") // Exempel på hur man kan generera ett användarnamn
                val biography = "Bio från Facebook-användare"
                val imageUri =
                    Uri.parse("android.resource://com.example.photoquestv3/${R.drawable.facebook}")
                auth.createAccount(
                    email,
                    "why do we have it?",
                    name,
                    username,
                    imageUri,
                    biography,
                    {
                        Log.d("!!!", "Kontot skapades framgångsrikt!")
                    },
                    { exception ->
                        Log.d("!!!", "Det gick inte att skapa kontot: ${exception.message}")
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

    private fun handleFacebookAccessToken(accessToken: AccessToken) {
//get credential
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener {
                Toast.makeText(requireActivity(), "Mess", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener { result ->
                val email = result.user!!.email
                Toast.makeText(requireActivity(), "Yay", Toast.LENGTH_SHORT).show()
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

    //gets Google auth token and creates a firebase account with it.
//skips the process of creating a new user object and goes to ChatActivity is token is already in the system
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
                        } else {
                            auth.createAccount(
                                currentUser.email ?: "",
                                "why do we have it?",
                                currentUser.displayName ?:"",
                                currentUser.displayName ?:"",
                                Uri.parse("android.resource://com.example.photoquestv3/${R.drawable.google}"),
                                "biography",
                                {
                                    Log.d("!!!", "Kontot skapades framgångsrikt!")
                                },
                                { exception ->
                                    Log.d("!!!", "Det gick inte att skapa kontot: ${exception.message}")
                                })


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

