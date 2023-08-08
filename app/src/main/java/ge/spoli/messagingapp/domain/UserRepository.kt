package ge.spoli.messagingapp.domain


import android.net.Uri
import ge.spoli.messagingapp.domain.user.UserEntity

interface UserRepository {
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
    fun loadUsers(
        searchParam: String,
        setWholeData: (users: List<UserEntity>) -> Unit,
        setUpdateData: (users: List<UserEntity>) -> Unit,
        setError: (error: String) -> Unit,
        lastUserName: String? = null
    )
}