package ge.spoli.messagingapp.user.views.viewholders

import androidx.recyclerview.widget.RecyclerView
import ge.spoli.messagingapp.chat.views.ChatActivity
import ge.spoli.messagingapp.databinding.UserListEntryBinding
import ge.spoli.messagingapp.user.domain.UserEntity
import ge.spoli.messagingapp.user.views.UserActivity

class UserListEntryViewHolder(
    private var binding: UserListEntryBinding
): RecyclerView.ViewHolder(binding.root) {
    fun update(user: UserEntity, activity: UserActivity) {

    }

    private fun openMessagingActivity(activity: UserActivity, userId: String, profilePic: String, username: String, jobInfo: String) {
        ChatActivity.startChatActivity(activity, userId, profilePic, username, jobInfo)
    }
}