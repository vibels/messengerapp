package ge.spoli.messagingapp.presentation.chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.spoli.messagingapp.common.MutableLiveQueue
import ge.spoli.messagingapp.domain.MessageRepository
import ge.spoli.messagingapp.domain.chat.ChatMessage
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(val messageRepository: MessageRepository): ViewModel() {
    private var _homePageMessages = MutableLiveData<List<HomePageMessage>>()
    private var _chatMessages = MutableLiveData<List<ChatMessage>>()
    private var _lastMessage = MutableLiveQueue<HomePageMessage>()
    private var _homePageError = MutableLiveData<String>()
    private var _chatError = MutableLiveData<String>()
    private var searchParam: String = ""
    val homePageError: LiveData<String> get() = _homePageError
    val chatError: LiveData<String> get() = _chatError

    val lastMessage: LiveData<HomePageMessage> get() = _lastMessage
    val homePageMessages: LiveData<List<HomePageMessage>> get() = _homePageMessages
    val chatMessages: LiveData<List<ChatMessage>> get() = _chatMessages

    private fun setLastMessage(message: HomePageMessage) {
        _lastMessage.postValue(message)
    }

    private fun setLastMessages(messages: List<HomePageMessage>) {
        _homePageMessages.postValue(messages)
    }

    private fun setChatMessages(messages: List<ChatMessage>) {
        _chatMessages.postValue(messages)
    }

    private fun setChatError(error: String) {
        _chatError.postValue(error)
    }

    private fun setHomePageError(error: String) {
        _homePageError.postValue(error)
    }

    fun loadLastMessages() {
        viewModelScope.launch {
            delay(500)
            messageRepository.getMessages(searchParam, ::setLastMessages, ::setHomePageError)
        }
    }

    fun fillDestinationInfo(message: HomePageMessage) {
        viewModelScope.launch {
            messageRepository.fillDestinationInfo(message, ::setLastMessage, ::setHomePageError)
        }
    }

    fun sendMessage(destinationId: String, message: ChatMessage) {
       viewModelScope.launch {
           messageRepository.sendMessage(destinationId, message, ::setChatError)
       }
    }

    fun setSearchParam(input: String) {
        searchParam = input
        loadLastMessages()
    }

    fun loadChatMessages(destinationId: String) {
        viewModelScope.launch {
            messageRepository.loadChatMessages(destinationId, ::setChatMessages, ::setChatError)
        }
    }
}