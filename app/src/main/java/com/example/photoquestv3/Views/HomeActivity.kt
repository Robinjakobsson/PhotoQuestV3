package com.example.photoquestv3.Views

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import com.example.photoquestv3.R
import com.example.photoquestv3.Views.Fragments.LoginFragment
import com.example.photoquestv3.Fragments.RegisterFragment
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
            Log.d("Hello world","asdasd")
        }
        binding.signInButton.setOnClickListener {
            startLoginFragment()
            Log.d("Hello world","asdasdasdadadada")
        }
    }

    fun startLoginFragment() {
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFrameLayout, loginFragment, "LoginFragment")
            .addToBackStack(null)
            .commit()

        //do not remove, need to check buttons behaviour.
        // Most likely should make isAvailable and isVisible = true upon closing Register or login fragments
//        binding.signInButton.isGone = true
//        binding.signUpButton.isGone = true
    }

    fun startRegisterFragment() {
        val registerFragment = RegisterFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registerFrameLayout, registerFragment, "RegisterFragment")
            .addToBackStack(null)
            .commit()
    }
}