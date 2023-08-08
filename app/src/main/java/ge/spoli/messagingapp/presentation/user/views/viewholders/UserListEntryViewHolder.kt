package ge.spoli.messagingapp.presentation.user.views.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.presentation.chat.views.ChatActivity
import ge.spoli.messagingapp.databinding.UserListEntryBinding
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.user.views.UserActivity

class UserListEntryViewHolder(
    private var binding: UserListEntryBinding
): RecyclerView.ViewHolder(binding.root) {
    fun update(user: UserEntity, activity: UserActivity) {
        val storageRef = Firebase.storage.reference.child(user.profile)
        GlideApp.with(activity)
            .load(storageRef)
            .into(binding.profilePictureImage)

        binding.username.text = user.username
        binding.jobInfo.text = user.jobInfo

        binding.root.setOnClickListener {
            openMessagingActivity(activity, user.id, user.profile, user.username, user.jobInfo)
        }
    }

    private fun openMessagingActivity(activity: UserActivity, userId: String, profilePic: String, username: String, jobInfo: String) {
        ChatActivity.startChatActivity(activity, userId, profilePic, username, jobInfo)
    }
}