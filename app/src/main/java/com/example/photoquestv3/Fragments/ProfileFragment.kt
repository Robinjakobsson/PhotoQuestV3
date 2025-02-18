package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var signOutButton: Button
    private lateinit var auth: AuthViewModel
    lateinit var authUser: FirebaseAuth
    lateinit var fireStoreVm: FireStoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireStoreVm = ViewModelProvider(this)[FireStoreViewModel::class.java]
        auth = ViewModelProvider(this)[AuthViewModel::class.java]

       // Viewmodel observer for user profile image.
        fireStoreVm.profileImage.observe(viewLifecycleOwner){imageUrl ->
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.family)
                .into(binding.profileImageImageView)
        }

        //  fun for getting user profile image from viewmodel.
        fireStoreVm.fetchProfileImage()

        auth.username.observe(viewLifecycleOwner){userName ->
            binding.profileNameTextView.text = userName
        }

        auth.fetchUserName()

        fireStoreVm.userQuote.observe(viewLifecycleOwner){userQuote ->
            binding.userQuoteTextView.text = userQuote
        }
        fireStoreVm.fetchUserQuote()





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