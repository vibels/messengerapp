package ge.spoli.messagingapp.presentation.chat.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.databinding.ActivityChatBinding
import ge.spoli.messagingapp.domain.chat.ChatMessage
import ge.spoli.messagingapp.presentation.chat.viewmodels.ChatViewModel

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    val viewModel: ChatViewModel by viewModels()

    private lateinit var binding: ActivityChatBinding
    private val destinationId: String by lazy { intent.getStringExtra(Constants.ID)!! }
    private val destinationProfile: String by lazy { intent.getStringExtra(Constants.PROFILE)!! }
    private val destinationUsername: String by lazy { intent.getStringExtra(Constants.USERNAME)!! }
    private val destinationJobInfo: String by lazy { intent.getStringExtra(Constants.JOB_INFO)!! }

    private val adapter = ChatViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.chatError.observe(this) {
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        viewModel.chatMessages.observe(this) {
            messagesLoaded(it)
        }
        setup()
        load()
    }

    private fun load() {
        viewModel.loadChatMessages(destinationId)
    }

    private fun setup() {
        binding.chat.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        binding.chat.adapter = adapter

        binding.jobInfo.text = destinationJobInfo
        binding.username.text = destinationUsername

        binding.back.setOnClickListener { onBackPressed() }

        val storageRef = Firebase.storage.reference.child(destinationProfile)
        GlideApp.with(this)
            .load(storageRef)
            .into(binding.profilePictureImage)

        binding.send.setOnClickListener {
            val now = System.currentTimeMillis()
            viewModel.sendMessage(
                destinationId,
                ChatMessage(
                    binding.input.text.toString(),
                    true,
                    now
                )
            )
            binding.input.setText("")
        }

    }

    private fun messagesLoaded(messages: List<ChatMessage>) {
        adapter.setMessages(messages.sortedByDescending { it.date })
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun startChatActivity(
            context: Context,
            id: String,
            profile: String,
            username: String,
            jobInfo: String
        ) {
            val myIntent = Intent(context, ChatActivity::class.java)
            myIntent.putExtra(Constants.ID, id)
            myIntent.putExtra(Constants.PROFILE, profile)
            myIntent.putExtra(Constants.USERNAME, username)
            myIntent.putExtra(Constants.JOB_INFO, jobInfo)
            context.startActivity(myIntent)
        }
    }


}