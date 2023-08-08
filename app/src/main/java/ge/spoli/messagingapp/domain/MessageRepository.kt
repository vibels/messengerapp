package ge.spoli.messagingapp.domain

import ge.spoli.messagingapp.domain.chat.ChatMessage
import ge.spoli.messagingapp.domain.chat.HomePageMessage

interface MessageRepository {
    fun fillDestinationInfo(
        message: HomePageMessage,
        setResult: (message: HomePageMessage) -> Unit,
        setError: (error: String) -> Unit
    )

    fun getMessages(input: String, setResult: (messages: List<HomePageMessage>) -> Unit, setError: (error: String) -> Unit)
    fun sendMessage(destinationId: String, message: ChatMessage, setError: (error: String) -> Unit)
    fun loadChatMessages(
        destinationId: String,
        setResult: (messages: List<ChatMessage>) -> Unit,
        setError: (error: String) -> Unit)
}