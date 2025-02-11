package com.example.photoquestv3.Views

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.photoquestv3.Fragments.RegisterFragment
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.example.photoquestv3.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.signUpButton.setOnClickListener {
            startRegisterFragment()
            
        }
        binding.signInButton.setOnClickListener {
            startLoginFragment()
        }
    }

    fun startLoginFragment() {
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFrameLayout, loginFragment, "LoginFragment")
            .addToBackStack(null)
            .commit()

        binding.signInButton.isGone = true
       binding.signUpButton.isGone = true
    }

    fun startRegisterFragment() {
        val registerFragment = RegisterFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registerFrameLayout, registerFragment, "RegisterFragment")
            .addToBackStack(null)
            .commit()
        binding.signInButton.isGone = true
        binding.signUpButton.isGone = true
    }
    fun getButtonsBack () {
        binding.signUpButton.isVisible = true
        binding.signInButton.isVisible = true
        binding.signUpButton.isClickable = true
        binding.signInButton.isVisible = true
    }
}