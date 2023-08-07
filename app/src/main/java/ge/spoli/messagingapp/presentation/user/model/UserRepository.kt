package ge.spoli.messagingapp.presentation.user.model


import android.net.Uri
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import ge.spoli.messagingapp.domain.user.UserEntity

interface UserRepository {
    fun getMessages(input: String, setResult: (messages: List<HomePageMessage>) -> Unit, setError: (error: String) -> Unit)
    fun fetchUser(id: String, setResult: (id: String) -> Unit, setError: (error: String) -> Unit)
    fun save(id: String, username: String, jobInfo: String, setError: (error: String) -> Unit)
    fun getUser(setResult: (user: UserEntity) -> Unit, setError: (error: String) -> Unit)
    fun updateUser(
        username: String,
        jobInfo: String,
        profile: String? = null,
        data: Uri? = null,
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    )

    fun signOut(setResult: (user: UserEntity?) -> Unit, setError: (error: String) -> Unit)
    fun fillDestinationInfo(
        message: HomePageMessage,
        setResult: (message: HomePageMessage) -> Unit,
        setError: (error: String) -> Unit
    )
}