package ge.spoli.messagingapp.user.domain

import androidx.annotation.Keep

@Keep
data class UserEntity(
    val id: Int = 0,
    val username: String,
    val jobInfo: String,
    val profile: String,
)