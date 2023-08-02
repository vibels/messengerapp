package ge.spoli.messagingapp.presentation.main.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ge.spoli.messagingapp.databinding.ActivityMainBinding
import ge.spoli.messagingapp.presentation.main.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.getViewModelFactory(
            applicationContext
        )
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val authService = Firebase.auth
        if (authService.currentUser == null) {
            println("send to login")
        } else {
            println("send to main page")
        }
    }

    private fun listenToLiveData() {
        viewModel.testLiveData.observe(this) {

        }
    }
}