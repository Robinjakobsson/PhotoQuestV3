package com.example.photoquestv3.Views

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.photoquestv3.Fragments.ChallengesFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.Fragments.HomeFragment
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.Fragments.PostFragment
import com.example.photoquestv3.Views.Fragments.ProfileFragment
import com.example.photoquestv3.Views.Fragments.SearchFragment
import com.example.photoquestv3.databinding.ActivityFeedBinding
import kotlinx.coroutines.processNextEventInCurrentThread

class FeedActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedBinding
    lateinit var auth : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = ViewModelProvider(this).get(AuthViewModel::class.java)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets

        }
        startHomeFragment()


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.star -> replaceFragment(ChallengesFragment())
                R.id.post -> replaceFragment(PostFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.profile -> {navigateToProfile()}
                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    //  fun for replacing fragment.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit()
    }
    private fun startHomeFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,HomeFragment()).commit()
    }
    private fun navigateToProfile(){
        val bundle = Bundle()

        val currentUserUid = auth.getCurrentUser()?.uid

        bundle.putString("uid",currentUserUid)

        val profile = ProfileFragment()

        profile.arguments = bundle

        replaceFragment(profile)
    }
}
