package ge.spoli.messagingapp.user.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ge.spoli.messagingapp.databinding.ActivityUsersBinding
import ge.spoli.messagingapp.user.viewmodel.UserViewModel

class UserActivity : AppCompatActivity() {
    private val viewModel: UserViewModel by viewModels {
        UserViewModel.getViewModelFactory(
            applicationContext
        )
    }

    private lateinit var binding: ActivityUsersBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        load()
    }


    private fun setup() {

    }

    private fun load() {

    }

    private fun listenToLiveData() {
        viewModel.testLiveData.observe(this) {

        }
    }
}