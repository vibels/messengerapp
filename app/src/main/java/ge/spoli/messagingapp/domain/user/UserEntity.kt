package ge.spoli.messagingapp.domain.user

import androidx.annotation.Keep

@Keep
data class UserEntity(
    val id: String,
    val username: String,
    val jobInfo: String,
    val profile: String,
)