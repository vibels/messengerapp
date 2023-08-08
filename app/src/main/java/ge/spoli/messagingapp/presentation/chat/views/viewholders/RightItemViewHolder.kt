package ge.spoli.messagingapp.presentation.chat.views.viewholders

import androidx.recyclerview.widget.RecyclerView
import ge.spoli.messagingapp.common.convertTimestamp
import ge.spoli.messagingapp.databinding.RightMessageBinding
import ge.spoli.messagingapp.domain.chat.ChatMessage

class RightItemViewHolder(
    private val binding: RightMessageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun setMessage(message: ChatMessage) {
        binding.date.text = message.date.convertTimestamp()
        binding.message.text = message.messageContent ?: ""
    }

}
