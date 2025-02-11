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
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.FeedActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.OnBackPressedCallback
import com.example.photoquestv3.Views.HomeActivity


class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (activity as? HomeActivity)?.getButtonsBack()
                    parentFragmentManager.beginTransaction().remove(this@LoginFragment).commit()
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginEmail: EditText = view.findViewById(R.id.login_email)
        val loginPw: EditText = view.findViewById(R.id.login_password)
        val buttonLogin: Button = view.findViewById(R.id.button_login)

        buttonLogin.setOnClickListener {
            val userEmail = loginEmail.text.toString().trim()
            val userPw = loginPw.text.toString().trim()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, userPw)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startFeedActivity()

                    } else {
                        Log.w("!!!", "Login failed", task.exception)
                    }
                }
        }
    }

    fun startFeedActivity() {
        val intent = Intent(requireActivity(), FeedActivity::class.java)
        requireActivity().startActivity(intent)
    }
    }