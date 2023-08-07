package ge.spoli.messagingapp.presentation.user.model

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import ge.spoli.messagingapp.domain.user.UserEntity
import java.util.Collections.synchronizedList

class UserRepositoryImpl : UserRepository {

    private var user: UserEntity? = null
    private var cachedUsers = synchronizedList(mutableListOf<HomePageMessage>())

    init {
        Firebase.database.setPersistenceEnabled(true)
    }

    companion object {
        const val USERS = "users"
        const val USERNAME = "username"
        const val JOB_INFO = "job_info"
        const val PROFILE = "profile"
        const val MESSAGE = "message"
        const val MESSAGES = "messages"
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

    override fun getMessages(
        input: String,
        setResult: (messages: List<HomePageMessage>) -> Unit,
        setError: (error: String) -> Unit
    ) {
        if (!validateLoggedUser(setError)) {
            return
        }
        val messagesRef = Firebase.database.getReference(MESSAGES)

        val id = user?.id

        messagesRef
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        setResult(emptyList())
                        return
                    }

                    val chats = (dataSnapshot.value as HashMap<*, *>)
                        .filter { (it.key as String).contains(id!!) }
                    val messages = chats
                        .toList()
                        .map { entry ->
                            mapChatToMessages(entry, id!!)
                        }
                    updateLastMessages(setResult, messages, input)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    setError(databaseError.message)
                }
            })
    }

    override fun fillDestinationInfo(
        message: HomePageMessage,
        setResult: (message: HomePageMessage) -> Unit,
        setError: (error: String) -> Unit
    ) {
        val userRef = Firebase.database.getReference(USERS).child(message.id)

        userRef.get().addOnSuccessListener {
            val userMap = (it.value as HashMap<*, *>)
            val messageFilled = HomePageMessage(
                id = message.id,
                message = message.message,
                profile = userMap[PROFILE].toString(),
                date = message.date,
                username = userMap[USERNAME].toString(),
                jobInfo = userMap[JOB_INFO].toString(),
            )
            val indexOfDestination = cachedUsers.indexOfFirst { user -> user.id == message.id }
            cachedUsers[indexOfDestination] = messageFilled
            setResult(messageFilled)
        }.addOnFailureListener {
            setError(it.message ?: "Unexpected error occurred")
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

    private fun mapChatToMessages(messageEntry: Pair<Any, Any>, id: String): HomePageMessage {
        val messageLabel = messageEntry.first.toString()
        val destinationId = messageLabel.split("-").filter { it != id }[0]
        val lastMessageEntry = (messageEntry.second as HashMap<*, *>)
            .entries.maxByOrNull { entry ->
                entry.key.toString()
            }!!

        val lastMessage = (lastMessageEntry.value as HashMap<*, *>)
        val destinationUser =
            cachedUsers.firstOrNull { user -> user.id == destinationId }
        val message = HomePageMessage(
            destinationId,
            lastMessage[MESSAGE].toString(),
            destinationUser?.profile,
            lastMessageEntry.key.toString().toLong(),
            destinationUser?.username,
            destinationUser?.jobInfo
        )
        val found =
            cachedUsers.indexOfFirst { user -> user.id == destinationId }
        if (found != -1) {
            cachedUsers[found] = message
        } else {
            cachedUsers.add(message)
        }
        return message
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

    private fun updateLastMessages(
        setResult: (messages: List<HomePageMessage>) -> Unit,
        messages: List<HomePageMessage>,
        input: String
    ) {
        setResult(messages.filter { it.username == null || it.username.contains(input) })
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