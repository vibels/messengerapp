import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.common.GlideApp
import ge.spoli.messagingapp.common.convertTimestamp
import ge.spoli.messagingapp.databinding.HomePageListEntryBinding
import ge.spoli.messagingapp.domain.chat.HomePageMessage

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

        binding.messageDate.text = message.date.convertTimestamp()

        binding.root.setOnClickListener {
//            ChatActivity
        }
    }

}