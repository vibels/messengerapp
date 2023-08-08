package ge.spoli.messagingapp.presentation.chat.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.databinding.LeftMessageBinding
import ge.spoli.messagingapp.databinding.RightMessageBinding
import ge.spoli.messagingapp.domain.chat.ChatMessage
import ge.spoli.messagingapp.presentation.chat.views.viewholders.LeftItemViewHolder
import ge.spoli.messagingapp.presentation.chat.views.viewholders.RightItemViewHolder

class ChatViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == Constants.LEFT) {
            LeftItemViewHolder(LeftMessageBinding.inflate(inflater, parent, false))
        } else {
            RightItemViewHolder(RightMessageBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].isSender) {
            return Constants.RIGHT
        }
        return Constants.LEFT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == Constants.LEFT) {
            (holder as LeftItemViewHolder).setMessage(messages[position])
        } else {
            (holder as RightItemViewHolder).setMessage(messages[position])
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMessages(value: List<ChatMessage>) {
        messages.clear()
        messages.addAll(value)
        notifyDataSetChanged()
    }

}