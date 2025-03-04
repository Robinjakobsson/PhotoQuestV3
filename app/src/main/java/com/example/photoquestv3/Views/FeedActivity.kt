package com.example.photoquestv3.Views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.photoquestv3.Fragments.ChallengesFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.photoquestv3.API.ApiWorker
import com.example.photoquestv3.Models.Post
import com.example.photoquestv3.Adapter.PostAdapter
import com.example.photoquestv3.BaseActivity
import com.example.photoquestv3.R
import com.example.photoquestv3.Fragments.HomeFragment
import com.example.photoquestv3.Repositories.ApiRepository
import com.example.photoquestv3.ViewModel.ApiViewModel
import com.example.photoquestv3.ViewModel.AuthViewModel
import com.example.photoquestv3.Views.Fragments.PostFragment
import com.example.photoquestv3.Views.Fragments.ProfileFragment
import com.example.photoquestv3.Views.Fragments.SearchFragment
import com.example.photoquestv3.databinding.ActivityFeedBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FeedActivity : BaseActivity() {
    lateinit var binding: ActivityFeedBinding
    lateinit var auth: AuthViewModel
    lateinit var apiRepository: ApiRepository
    lateinit var apiViewModel: ApiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = ViewModelProvider(this).get(AuthViewModel::class.java)
        apiRepository = ApiRepository()

        apiViewModel = ViewModelProvider(this)[ApiViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets

        }

        dailyPhotoAPICall(this)
        startHomeFragment()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.star -> replaceFragment(ChallengesFragment())
                R.id.post -> replaceFragment(PostFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.profile -> {
                    navigateToProfile()
                }

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
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, HomeFragment())
            .commit()
    }

    private fun navigateToProfile() {
        val bundle = Bundle()

        val currentUserUid = auth.getCurrentUser()?.uid

        bundle.putString("uid", currentUserUid)

        val profile = ProfileFragment()

        profile.arguments = bundle

        replaceFragment(profile)
    }

    fun dailyPhotoAPICall(context: Context) {


        Log.d("API", "Works...")
        val currentTime = Calendar.getInstance()

        Log.d("!!!", "${currentTime.time}")

        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.HOUR_OF_DAY, 24)
        }

        val delay = dueTime.timeInMillis - currentTime.timeInMillis

        val dailyAPIRequest = OneTimeWorkRequestBuilder<ApiWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "DailyPhotoFetch",
            ExistingWorkPolicy.KEEP,
            dailyAPIRequest
        )
    }
}
