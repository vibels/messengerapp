package ge.spoli.messagingapp.model

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.domain.UserRepository
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
        if (!validateLoggedUser(setError)) {
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
        if (!validateLoggedUser(setError)) {
            return
        }
        try {
            var profValue = profile
            if (profValue == null) {
                profValue = Constants.DEFAULT_PROFILE
            } else if (data != null) {
                val ref = Firebase.storage.reference.child(profValue)
                val task = ref.putFile(data)
                task.addOnFailureListener {
                    setError(
                        it.message
                            ?: "Failure uploading picture, ideally should reset profile, but it's out of scope"
                    )
                }.addOnSuccessListener {
                    continueUpdate(username, jobInfo, profValue, setResult, setError)
                }
            }
            continueUpdate(username, jobInfo, profValue, setResult, setError)
        } catch (ex: Exception) {
            setError(ex.message ?: "Unexpected error occurred")
        }
    }

    override fun signOut(
        setResult: (user: UserEntity?) -> Unit,
        setError: (error: String) -> Unit
    ) {
        user = null
        setResult(null)
    }

    override fun loadUsers(
        searchParam: String,
        setWholeData: (users: List<UserEntity>) -> Unit,
        setUpdateData: (users: List<UserEntity>) -> Unit,
        setError: (error: String) -> Unit,
        lastUserName: String?
    ) {
        if (searchParam.length < 3 && searchParam.isNotEmpty()) {
            return
        } else {
            var usersRef = Firebase.database.getReference(USERS).orderByChild(USERNAME)
            if (!lastUserName.isNullOrBlank()) {
                usersRef = usersRef.startAfter(lastUserName)
            }
            usersRef
                .limitToFirst(5).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            val users = (snapshot.value as HashMap<*, *>)
                            val list = users.entries.map { userEntry -> mapToUserEntity(userEntry) }
                                .filter { entry ->
                                    entry.id != user?.id && entry.username.contains(searchParam)
                                }.sortedBy { user -> user.username }

                            if (!lastUserName.isNullOrBlank()) {
                                setUpdateData(list)
                            } else {
                                setWholeData(list)
                            }
                        } else {
                            setUpdateData(emptyList())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        setError(error.message)
                    }
                })


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

    private fun mapToUserEntity(user: MutableMap.MutableEntry<out Any, out Any>): UserEntity {
        val id = user.key
        val mp = (user.value as HashMap<*, *>)
        return UserEntity(
            id.toString(),
            mp[USERNAME].toString(),
            mp[JOB_INFO].toString(),
            mp[PROFILE].toString(),
        )
    }

    private fun continueUpdate(
        username: String,
        jobInfo: String,
        profile: String,
        setResult: (user: UserEntity) -> Unit,
        setError: (error: String) -> Unit
    ) {
        if (username != user?.username) {
            Firebase.auth.currentUser?.updateEmail("$username@dummy.email")
                ?.addOnSuccessListener {
                    saveInternal(user?.id, username, jobInfo, profile, setError, setResult)
                }
        } else {
            saveInternal(user?.id, username, jobInfo, profile, setError, setResult)
        }
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
            setError("Id should not be null during saving")
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

    private fun validateLoggedUser(setError: (error: String) -> Unit): Boolean {
        if (user?.id.isNullOrBlank()) {
            setError("User not logged in")
            return false
        }
        return true
    }
}