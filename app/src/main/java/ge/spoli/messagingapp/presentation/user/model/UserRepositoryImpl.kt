package ge.spoli.messagingapp.presentation.user.model

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.domain.user.UserEntity

class UserRepositoryImpl : UserRepository {

    private var user: UserEntity? = null

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
        if (user == null) {
            setError("No logged in user found")
            return
        }
        setResult(user!!)
    }

    override fun updateUser(
        username: String,
        jobInfo: String,
        profile: String?,
        data: Uri?,
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    ) {
        try {
            var profValue = profile
            if (profValue == null) {
                profValue = Constants.DEFAULT_PROFILE
            } else if (data != null) {
                val ref = Firebase.storage.reference.child(profValue)
                val task = ref.putFile(data)
                task.addOnFailureListener {
                    setError(it.message ?: "Failure uploading picture, ideally should reset profile, but it's out of scope")
                }.addOnSuccessListener {
                    continueUpdate(username, jobInfo, profValue, setResult, setError)
                }
            }
            continueUpdate(username, jobInfo, profValue, setResult, setError)
        } catch (ex: Exception) {
            setError(ex.message ?: "Unexpected error occurred")
        }
    }

    override fun signOut(setResult: (user: UserEntity?) -> Unit,
                         setError: (error: String) -> Unit) {
        user = null
        setResult(null)
    }

    private fun continueUpdate(username: String, jobInfo: String, profile: String, setResult: (user: UserEntity) -> Unit, setError: (error: String) -> Unit) {
        if (username != user?.username) {
            Firebase.auth.currentUser?.updateEmail("$username@dummy.email")
                ?.addOnSuccessListener {
                    saveInternal(user?.id, username, jobInfo, profile, setError, setResult)
                }
        } else {
            saveInternal(user?.id, username, jobInfo, profile, setError, setResult)
        }
    }
    override fun fetchUser(
        id: String,
        setResult: (id: String) -> Unit,
        setError: (error: String) -> Unit
    ) {
        try {
            if (user?.id == id) {
                setResult(id)
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
                    setResult(id)
                }.addOnFailureListener {
                    setError("Error occured during db connection")
                }
        } catch (ex: Exception) {
            setError(ex.message ?: "Unexpected error occurred")
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
        id: String?,
        username: String,
        jobInfo: String,
        profile: String,
        setError: (error: String) -> Unit,
        setResult: ((user: UserEntity) -> Unit)? = null,
    ) {
        if (id == null) {
            setError( "Id should not be null during saving")
            return
        }
        try {
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
                setResult(user!!)
            }
        } catch (ex: Exception) {
            setError(ex.message ?: "Unexpected error occurred")
        }

    }
}