package ge.spoli.messagingapp.presentation.main.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ge.spoli.messagingapp.presentation.main.views.profile.ProfileFragment
import ge.spoli.messagingapp.R
import ge.spoli.messagingapp.databinding.ActivityMainBinding
import ge.spoli.messagingapp.presentation.main.viewmodel.MainViewModel
import ge.spoli.messagingapp.presentation.main.views.home.HomeFragment
import ge.spoli.messagingapp.presentation.user.views.UserActivity
import kotlin.Exception

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        switch(HomeFragment())

        binding.navigation.setOnItemSelectedListener {
            if (it.itemId == R.id.home) {
                switch(HomeFragment())
                return@setOnItemSelectedListener true
            }
            if (it.itemId == R.id.profile) {
                switch(ProfileFragment())
                return@setOnItemSelectedListener true
            }
            throw Exception("Something went wrong")
        }

        binding.fab.setOnClickListener {
            navigateToMainPage()
        }

        setContentView(binding.root)
    }

    private fun switch(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.frameFragment.id, fragment)
            .commit()
    }

    private fun navigateToMainPage() {
        val myIntent = Intent(this@MainActivity, UserActivity::class.java)
        this@MainActivity.startActivity(myIntent)
    }

}