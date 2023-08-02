package ge.spoli.messagingapp.presentation.entrypoint.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import ge.spoli.messagingapp.R
import ge.spoli.messagingapp.databinding.ActivityEntrypointBinding
import ge.spoli.messagingapp.presentation.main.views.MainActivity
import ge.spoli.messagingapp.presentation.entrypoint.viewmodels.EntryViewModel


@Suppress("MemberVisibilityCanBePrivate")
@AndroidEntryPoint
class EntrypointActivity : AppCompatActivity() {

    val entryViewModel: EntryViewModel by viewModels()

    private lateinit var binding: ActivityEntrypointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntrypointBinding.inflate(layoutInflater)
        setContentView(binding.root)
        entryViewModel.entrypointError.observe(this) {
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        entryViewModel.entryStatus.observe(this) {
            if (it != null) {
                startMainActivity()
            }
        }

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            logUserWithId(currentUser.uid)
        }

        setup()
    }

    override fun onResume() {
        super.onResume()
        setLoading(false)
    }

    private fun setup() {
        binding.signInButton.setOnClickListener {

            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            if (username.isEmpty()) {
                showError("Please enter username")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                showError("Please enter password")
                return@setOnClickListener
            }

            logIn(username, password)
            setLoading(true)
        }

        binding.signUpButton.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val jobInfo = binding.jobInfo.text.toString()

            if (username.isEmpty() || username.length < 3) {
                showError("Username should be at least 3 characters long")
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 3) {
                showError("Password should be at least 3 characters long")
                return@setOnClickListener
            }
            if (jobInfo.isEmpty() || jobInfo.length < 3) {
                showError("Job field should be at least 3 characters long")
                return@setOnClickListener
            }

            setLoading(true)
            register(username, password, jobInfo)
        }

        binding.registerButton.setOnClickListener {
            startRegisterFragment()
        }

        binding.password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (binding.jobInfo.visibility != View.VISIBLE && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideSoftKeyboard(this)
                binding.password.clearFocus()
                return@OnKeyListener true
            }
            false
        })

        binding.jobInfo.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideSoftKeyboard(this)
                binding.jobInfo.clearFocus()
                return@OnKeyListener true
            }
            false
        })

    }

    private fun setLoading(isLoading: Boolean) {
        binding.username.isEnabled = !isLoading
        binding.password.isEnabled = !isLoading
        binding.jobInfo.isEnabled = !isLoading
        binding.signInButton.isEnabled = !isLoading
        binding.signUpButton.isEnabled = !isLoading
        binding.loading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun startRegisterFragment() {
        binding.signInButton.visibility = View.GONE
        binding.jobInfo.visibility = View.VISIBLE
        binding.signUpButton.visibility = View.VISIBLE
        binding.register.text = getString(R.string.back_login)
        binding.registerButton.text = getString(R.string.back)
        binding.registerButton.setOnClickListener {
            backToLoginFragment()
        }
    }

    private fun backToLoginFragment() {
        binding.signInButton.visibility = View.VISIBLE
        binding.jobInfo.setText("")
        binding.jobInfo.visibility = View.GONE
        binding.signUpButton.visibility = View.GONE
        binding.register.text = getString(R.string.register)
        binding.registerButton.text = getString(R.string.sign_up)
        binding.registerButton.setOnClickListener {
            startRegisterFragment()
        }
    }

    private fun logUserWithId(id: String) {
        entryViewModel.loginWithId(id)
    }

    private fun logIn(username: String, password: String) {
        entryViewModel.login(username, password, this)
    }

    private fun register(username: String, password: String, jobInfo: String) {
        entryViewModel.saveUserInfo(username, password, jobInfo, this)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    private fun showError(error: String) {
        setLoading(false)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun hideSoftKeyboard(activity: Activity?) {
        if (activity == null) return
        if (activity.currentFocus == null) return
        val inputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    }

}