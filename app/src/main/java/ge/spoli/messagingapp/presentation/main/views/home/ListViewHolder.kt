package ge.spoli.messagingapp.presentation.main.views.home

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.common.convertToTimePassed
import ge.spoli.messagingapp.databinding.HomePageListEntryBinding
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import ge.spoli.messagingapp.presentation.chat.views.ChatActivity

class ListViewHolder(
    private val binding: HomePageListEntryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setMessage(message: HomePageMessage) {
        val storageReference = Firebase.storage.reference
            .child(message.profile?: Constants.DEFAULT_PROFILE)
        GlideApp.with(binding.root.context)
            .load(storageReference)
            .into(binding.profilePictureImage)

        binding.message.text = message.message
        binding.username.text = message.username ?: ""

        binding.messageDate.text = message.date.convertToTimePassed()

        binding.root.setOnClickListener {
            ChatActivity.startChatActivity(binding.root.context, message.id, message.profile ?: Constants.DEFAULT_PROFILE,
                message.username ?: "", message.jobInfo ?: "")
        }
    }

}