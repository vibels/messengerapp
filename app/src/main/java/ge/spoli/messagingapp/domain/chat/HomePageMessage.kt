package ge.spoli.messagingapp.domain.chat

import androidx.annotation.Keep

@Keep
data class HomePageMessage(
    val id: String,
    val message: String,
    val profile: String?,
    val date: Long,
    val username: String?,
    val jobInfo: String?
)