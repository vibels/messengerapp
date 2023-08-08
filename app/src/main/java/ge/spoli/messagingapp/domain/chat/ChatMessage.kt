package ge.spoli.messagingapp.domain.chat

import androidx.annotation.Keep

@Keep
data class ChatMessage(
    var messageContent: String?,
    var isSender: Boolean,
    var date: Long,
)