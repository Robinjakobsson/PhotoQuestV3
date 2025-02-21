package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.photoquestv3.Adapter.ProfileAdapter
import com.example.photoquestv3.Fragments.SettingsFragment
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    private lateinit var auth: AuthViewModel
    lateinit var authUser: FirebaseAuth
    lateinit var fireStoreVm: FireStoreViewModel
    private lateinit var user: User
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        fireStoreVm = ViewModelProvider(this)[FireStoreViewModel::class.java]
        fireStoreVm.userImages.observe(viewLifecycleOwner){images -> profileAdapter.updateData(images) }

        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        profileAdapter = ProfileAdapter(emptyList())
        authUser = Firebase.auth

        arguments?.getString("uid")?.let { uid ->
            lifecycleScope.launch {
                user = fireStoreVm.fetchUserData(uid) ?: return@launch
                updateUserData()
                fireStoreVm.loadUserImages(uid)
                layoutManager()

            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followButton.setOnClickListener {
            auth.getCurrentUser()?.let { currentUser ->
                fireStoreVm.followUser(currentUser.uid, targetUserId = user.uid)
            }
        }

        binding.buttonLogout.setOnClickListener{
            authUser.signOut()  //changed places of those two, otherwise sees HomeActivity that user is signed in
            returnHomeActivity()
        }
        binding.profileSettingButton.setOnClickListener {
            startSettingsFragment()
        }
    }

    private fun startSettingsFragment() {
        val settingsFragment = SettingsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, settingsFragment, "settingsFragment")
            .commit()
    }



    private fun returnHomeActivity() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun updateUserData() {
        binding.profileNameTextView.text = user.name
        binding.userQuoteTextView.text = user.biography
        binding.profileFollowerTextView.text = user.followers.size.toString()

        Glide.with(requireContext())
            .load(user.imageUrl)
            .placeholder(R.drawable.photo)
            .into(binding.profileImageImageView)
    }

    private fun layoutManager(){
        binding.profileRecycler.apply{
            layoutManager = GridLayoutManager(context,2)
            adapter = profileAdapter
        }
    }


}