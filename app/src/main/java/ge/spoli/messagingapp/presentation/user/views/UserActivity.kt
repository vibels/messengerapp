package ge.spoli.messagingapp.presentation.user.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ge.spoli.messagingapp.databinding.ActivityUsersBinding

class UserActivity : AppCompatActivity() {
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

}