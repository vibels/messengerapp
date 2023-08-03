package ge.spoli.messagingapp.presentation.user.model

import com.google.firebase.auth.ktx.auth
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

    override fun getUser(
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    ) {
        setResult(user)
    }

    override fun updateUser(
        username: String,
        jobInfo: String,
        profile: String,
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    ) {
        Firebase.auth.currentUser?.updateEmail("$username@dummy.email")

        saveInternal(user.id, username, jobInfo, profile, setError, setResult)
    }

    override fun fetchUser(
        id: String,
        setResult: (code: Int) -> Unit,
        setError: (error: String) -> Unit
    ) {
        if (this::user.isInitialized && user.id == id) {
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
        saveInternal(id, username, jobInfo, Constants.DEFAULT_PROFILE, setError)
    }

    private fun saveInternal(
        id: String,
        username: String,
        jobInfo: String,
        profile: String,
        setError: (error: String) -> Unit,
        setResult: ((user: UserEntity) -> Unit)? = null,
    ) {
        val database = Firebase.database
        val usersReference = database.getReference(USERS)
        val userReference = usersReference.child(id)

        userReference.child(JOB_INFO).setValue(jobInfo)
            .continueWithTask {
                userReference.child(PROFILE).setValue(profile)
            }.continueWithTask {
                userReference.child(USERNAME).setValue(username)
            }.addOnFailureListener {
                setError(it.message ?: "Something went wrong")
            }
        val updated = UserEntity(id, username, jobInfo, profile)
        user = updated
        if (setResult != null) {
            setResult(user)
        }
    }
}