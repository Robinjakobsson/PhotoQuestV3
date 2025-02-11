package com.example.photoquestv3.Views

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.Fragments.HomeFragment
import com.example.photoquestv3.Views.Fragments.PostFragment
import com.example.photoquestv3.Views.Fragments.ProfileFragment
import com.example.photoquestv3.Views.Fragments.SearchFragment
import com.example.photoquestv3.Views.Fragments.StarFragment
import com.example.photoquestv3.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFeedBinding.inflate(layoutInflater)
        mockData()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }


        //  Bottom nav flow.
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.star -> replaceFragment (StarFragment())
                R.id.post -> replaceFragment (PostFragment())
                R.id.search -> replaceFragment (SearchFragment())
                R.id.profile -> replaceFragment (ProfileFragment())
                else -> return@setOnItemSelectedListener false
            }
            true

        }



    }

    //  fun for replacing fragment.
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragment).commit()
    }

    private fun mockData() {

        val postList = Post.mockData()
        val postAdapter = PostAdapter(postList)

        binding.recContainer.layoutManager = LinearLayoutManager(this)
        binding.recContainer.adapter = postAdapter


    }

}