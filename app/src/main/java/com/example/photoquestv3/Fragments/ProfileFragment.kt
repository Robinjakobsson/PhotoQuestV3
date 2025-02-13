package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ProfileFragment : Fragment() {

    lateinit var signOutButton: Button
    private lateinit var auth: AuthViewModel
    lateinit var authUser: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authUser = Firebase.auth
        signOutButton = view.findViewById(R.id.button_logout)
        signOutButton.setOnClickListener{
            authUser.signOut()  //changed places of those two, otherwise sees HomeActivity that user is signed in
            returnHomeActivity()

        }
    }

    fun returnHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

}