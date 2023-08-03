package ge.spoli.messagingapp.presentation.user.model


import ge.spoli.messagingapp.domain.user.UserEntity

interface UserRepository {
    fun fetchUser(id: String, setResult: (code: Int) -> Unit, setError: (error: String) -> Unit)
    fun save(id: String, username: String, jobInfo: String, setError: (error: String) -> Unit)
    fun getUser(setResult: (user: UserEntity) -> Unit, setError: (error: String) -> Unit)
    fun updateUser(
        username: String,
        jobInfo: String,
        profile: String,
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    )
}