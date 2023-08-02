package ge.spoli.messagingapp.presentation.user.model

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.domain.user.UserEntity

class UserRepositoryImpl : UserRepository {

    private lateinit var user: UserEntity

    init {
        Firebase.database.setPersistenceEnabled(true)
    }

    companion object {
        const val USERS = "users"
        const val USERNAME = "username"
        const val JOB_INFO = "job_info"
        const val PROFILE = "profile"
    }

    override fun getUser() = user
    override fun fetchUser(
        id: String,
        setResult: (code: Int) -> Unit,
        setError: (error: String) -> Unit
    ) {
        if (this::user.isInitialized) {
            return
        }
        val database = Firebase.database
        val usersReference = database.getReference(USERS)
        val userReference = usersReference.child(id)
        userReference
            .get()
            .addOnSuccessListener {
                val userInfo = (it.value as HashMap<*, *>)
                user = UserEntity(
                    id,
                    userInfo[USERNAME].toString(),
                    userInfo[JOB_INFO].toString(),
                    userInfo[PROFILE].toString(),
                )
                setResult(Constants.SUCCESS)
            }.addOnFailureListener {
                setError("Error occured during db connection")
            }

    }

    override fun save(
        id: String,
        username: String,
        jobInfo: String,
        setError: (error: String) -> Unit
    ) {
        val database = Firebase.database
        val usersReference = database.getReference(USERS)
        val userReference = usersReference.child(id)

        userReference.child(JOB_INFO).setValue(jobInfo)
            .continueWithTask {
                userReference.child(PROFILE).setValue(Constants.DEFAULT_PROFILE)
            }.continueWithTask {
                userReference.child(USERNAME).setValue(username)
            }.addOnFailureListener {
                setError(it.message ?: "Something went wrong")
            }
    }
}