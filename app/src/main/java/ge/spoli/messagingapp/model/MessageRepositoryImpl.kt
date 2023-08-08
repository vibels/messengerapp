package ge.spoli.messagingapp.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ge.spoli.messagingapp.domain.MessageRepository
import ge.spoli.messagingapp.domain.chat.ChatMessage
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import java.util.Collections


class MessageRepositoryImpl : MessageRepository {
    private var cachedUsers = Collections.synchronizedList(mutableListOf<HomePageMessage>())

    companion object {
        const val MESSAGES = "messages"
        const val MESSAGE = "message"
        const val OWNER = "owner"
    }

    override fun getMessages(
        input: String,
        setResult: (messages: List<HomePageMessage>) -> Unit,
        setError: (error: String) -> Unit
    ) {
        val messagesRef = Firebase.database.getReference(MESSAGES)

        val id = Firebase.auth.currentUser?.uid!!

        messagesRef
            .orderByKey()
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value == null) {
                        setResult(emptyList())
                        return
                    }
                    val chats = (dataSnapshot.value as HashMap<*, *>)
                        .filter { (it.key as String).contains(id) }
                    val messages = chats
                        .toList()
                        .map { entry ->
                            mapChatToMessage(entry, id)
                        }
                    updateLastMessages(setResult, messages, input)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    setError(databaseError.message)
                }
            })
    }

    override fun sendMessage(
        destinationId: String,
        message: ChatMessage,
        setError: (String) -> Unit
    ) {
        val messagesRef = Firebase.database.getReference(MESSAGES)

        var id = Firebase.auth.currentUser?.uid!!

        val entry = getEntry(id, destinationId)

        messagesRef.child(entry)
            .get().addOnSuccessListener {
                val ref = messagesRef.child(entry)
                    .child(message.date.toString())

                if (!message.isSender) {
                    id = destinationId
                }
                val postValues = mapOf(OWNER to id, MESSAGE to message.messageContent!!)

                ref.updateChildren(postValues)
                    .addOnFailureListener {
                        setError(it.message ?: "Unexpected error occurred")
                    }
            }.addOnFailureListener {
                setError(it.message ?: "Unexpected error occurred")
            }
    }

    override fun loadChatMessages(
        destinationId: String,
        setResult: (messages: List<ChatMessage>) -> Unit,
        setError: (error: String) -> Unit
    ) {
        val messagesRef = Firebase.database.getReference(MESSAGES)

        val id = Firebase.auth.currentUser?.uid!!

        val entry = getEntry(id, destinationId)
        messagesRef.child(entry)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val messages = (snapshot.value as HashMap<*, *>)
                            .entries
                            .map { entry ->
                                mapToChatMessage(entry, id)
                            }
                        setResult(messages)
                    } else {
                        setResult(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    setError(error.message)
                }

            })
    }

    override fun fillDestinationInfo(
        message: HomePageMessage,
        setResult: (message: HomePageMessage) -> Unit,
        setError: (error: String) -> Unit
    ) {
        val id = message.id.split("-").filter { it != Firebase.auth.currentUser?.uid!! }[0]
        val userRef = Firebase.database.getReference(UserRepositoryImpl.USERS).child(id)

        userRef.get().addOnSuccessListener {
            val userMap = (it.value as HashMap<*, *>)
            val messageFilled = HomePageMessage(
                id = message.id,
                message = message.message,
                profile = userMap[UserRepositoryImpl.PROFILE].toString(),
                date = message.date,
                username = userMap[UserRepositoryImpl.USERNAME].toString(),
                jobInfo = userMap[UserRepositoryImpl.JOB_INFO].toString(),
            )
            val indexOfDestination = cachedUsers.indexOfFirst { user -> user.id == message.id }
            cachedUsers[indexOfDestination] = messageFilled
            setResult(messageFilled)
        }.addOnFailureListener {
            setError(it.message ?: "Unexpected error occurred")
        }
    }

    private fun getEntry(id: String, destinationId: String): String {
        // Need to check so that different users write in the same bucket
        return if (id < destinationId) {
            "$id-$destinationId"
        } else {
            "$destinationId-$id"
        }
    }

    private fun mapToChatMessage(
        entry: MutableMap.MutableEntry<out Any, out Any>,
        id: String
    ): ChatMessage {
        val date = entry.key.toString().toLong()
        val messageMap = (entry.value as HashMap<*, *>)

        return ChatMessage(
            messageMap[MESSAGE].toString(),
            messageMap[OWNER].toString() == id,
            date
        )
    }

    private fun mapChatToMessage(messageEntry: Pair<Any, Any>, id: String): HomePageMessage {
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

    private fun updateLastMessages(
        setResult: (messages: List<HomePageMessage>) -> Unit,
        messages: List<HomePageMessage>,
        input: String
    ) {
        setResult(messages.filter { it.username == null || input.isBlank() || it.username.contains(input) })
    }
}