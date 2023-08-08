package ge.spoli.messagingapp.presentation.user.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ge.spoli.messagingapp.databinding.ActivityUsersBinding
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.user.viewmodel.UserViewModel

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private val adapter = UserListViewAdapter(this)
    val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.userError.observe(this) {
            setLoading(false)
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        viewModel.users.observe(this) {
            setLoading(false)
            if (it.first) {
                append(it.second)
            } else {
                usersLoaded(it.second)
            }
        }

        setup()
        load()
    }


    private fun setup() {
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!adapter.loading && linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.users.size - 1) {
                    val size = adapter.users.size
                    if (size > 0) {
                        load(lastUsername = adapter.users[size - 1].username)
                    } else {
                        load()
                    }
                }
            }
        })
        binding.list.layoutManager = LinearLayoutManager(this)
        binding.list.adapter = adapter

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.search.setRequestListener {
            load(it,  true)
        }
    }

    private fun load(searchParam: String = binding.search.searchParam, loading : Boolean = false, lastUsername: String? = null ) {
        adapter.updateLoading()
        if (loading) {
            setLoading(true)
        }
        viewModel.loadUsers(searchParam, lastUsername)
    }

    private fun append(users: List<UserEntity>) {
        adapter.append(users)
    }

    private fun setLoading(value: Boolean) {
        if (value) {
            binding.usersProgressBar.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.usersProgressBar.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }
    private fun usersLoaded(users: List<UserEntity>) {
        if (users.isNotEmpty()) {
            binding.notFound.visibility = View.GONE
        } else {
            binding.notFound.visibility = View.VISIBLE
        }
        adapter.update(users)
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

}