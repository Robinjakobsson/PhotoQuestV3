package com.example.photoquestv3.Views.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.photoquestv3.Adapter.ProfileAdapter
import com.example.photoquestv3.Fragments.FollowerFragment
import com.example.photoquestv3.Fragments.SettingsFragment
import com.example.photoquestv3.Models.User
import com.example.photoquestv3.R
import com.example.photoquestv3.Repositories.ChallengesRepository
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.ViewModel.ChallengesViewModel
import com.example.photoquestv3.ViewModel.FireStoreViewModel
import com.example.photoquestv3.Views.HomeActivity
import com.example.photoquestv3.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!


    private lateinit var auth: AuthViewModel
    lateinit var authUser: FirebaseAuth
    lateinit var fireStoreVm: FireStoreViewModel
    private lateinit var user: User
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var challengesVm: ChallengesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        fireStoreVm = ViewModelProvider(this)[FireStoreViewModel::class.java]
        fireStoreVm.userImages.observe(viewLifecycleOwner) { images ->
            profileAdapter.updateData(
                images
            )
        }
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        profileAdapter = ProfileAdapter(emptyList())
        authUser = Firebase.auth
        challengesVm = ViewModelProvider(this)[ChallengesViewModel::class.java]



        arguments?.getString("uid")?.let { uid ->
            lifecycleScope.launch {
                user = fireStoreVm.fetchUserData(uid) ?: return@launch
                updateUserData()
                setupFollowButton()
                checkFollowingStatus()
                layoutManager()
                fireStoreVm.loadUserImages(uid)


                fireStoreVm.getFollowerCount(uid).observe(viewLifecycleOwner) { count ->
                    binding.profileFollowerTextView.text = count.toString()
                }


                // Shows how many challenges user has completed.
                challengesVm.countCompletedChallenges(uid)

                challengesVm.numberOfCompletedChallenges.observe(viewLifecycleOwner) { count ->
                    binding.profileHeartTextView.text = count.toString()

                }

                fireStoreVm.fetchUserPostCount(uid).observe(viewLifecycleOwner) { count ->
                    binding.profilePostTextView.text = count.toString()
                }
                showSettings()

            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.followButton.setOnClickListener {
            auth.getCurrentUser()?.let { currentUser ->
                fireStoreVm.checkFollowingStatus(currentUser.uid, user.uid)
                    .observe(viewLifecycleOwner) { isFollowing ->
                        if (isFollowing) {
                            fireStoreVm.unfollowUser(currentUser.uid, user.uid)
                        } else {
                            fireStoreVm.followUser(currentUser.uid, user.uid)
                        }
                        checkFollowingStatus()
                    }
            }
        }
        binding.profileFollowerButton.setOnClickListener { sendArguments() }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupFollowButton() {
        val currentUser = auth.getCurrentUser()
        if (currentUser?.uid == user.uid) {
            binding.followButton.visibility = View.GONE
        } else {
            binding.followButton.visibility = View.VISIBLE

            binding.followButton.setOnClickListener {
                if (binding.followButton.text.toString() == getString(R.string.follow)) {
                    binding.followButton.text = getString(R.string.unfollow)
                    if (currentUser != null) {
                        fireStoreVm.followUser(currentUser.uid, user.uid)
                    }
                } else {
                    binding.followButton.text = getString(R.string.follow)
                    if (currentUser != null) {
                        fireStoreVm.unfollowUser(currentUser.uid, user.uid)
                    }
                }
            }
        }
    }

    private fun startSettingsFragment() {
        val settingsFragment = SettingsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, settingsFragment, "settingsFragment")
            .commit()
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

    private fun checkFollowingStatus() {
        auth.getCurrentUser()?.let { currentUser ->
            fireStoreVm.checkFollowingStatus(currentUser.uid, user.uid)
                .observe(viewLifecycleOwner) { isFollowing ->
                    if (isFollowing) {
                        binding.followButton.text = getString(R.string.unfollow)
                    } else {
                        binding.followButton.text = getString(R.string.follow)
                    }
                }
        }
    }

    private fun layoutManager() {
        binding.profileRecycler.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = profileAdapter
        }
    }

    /**
     *
     * Shows the settingsbutton in users own profile, hides in other user's profile.
     */
    fun showSettings() {
        val currentUser = auth.getCurrentUser()

        if (currentUser?.uid != user.uid) {
            binding.profileSettingButton.visibility = View.GONE
        } else {
            binding.profileSettingButton.visibility = View.VISIBLE

            binding.profileSettingButton.setOnClickListener {
                startSettingsFragment()
            }
        }
    }
    fun sendArguments() {
        val bundle = Bundle()
        bundle.putString("userid",user.uid)

        val followerFragment = FollowerFragment()

        followerFragment.arguments = bundle

        followerFragment.show(parentFragmentManager,"bottomSheet")

    }
}