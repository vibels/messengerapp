package ge.spoli.messagingapp.presentation.main.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.domain.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class MainViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    private var _loggedUser = MutableLiveData<UserEntity?>()
    private var _mainViewError = MutableLiveData<String>()
    private var _homePageError = MutableLiveData<String>()
    private var _messages = MutableLiveData<List<HomePageMessage>>()
    private var _lastMessage = MutableLiveData<HomePageMessage>()
    private var searchParam: String = ""

    val lastMessage: LiveData<HomePageMessage> get() = _lastMessage
    val messages: LiveData<List<HomePageMessage>> get() = _messages
    val loggedUser: LiveData<UserEntity?> get() = _loggedUser
    val mainViewError: LiveData<String> get() = _mainViewError
    val homePageError: LiveData<String> get() = _homePageError


    private fun setLoggedUser(user: UserEntity?) {
        _loggedUser.postValue(user)
    }

    private fun setError(error: String) {
        _mainViewError.postValue(error)
    }

    private fun setLastMessage(message: HomePageMessage) {
        _lastMessage.postValue(message)
    }

    private fun setHomePageError(error: String) {
        _homePageError.postValue(error)
    }

    private fun setMessages(messages: List<HomePageMessage>) {
        _messages.postValue(messages)
    }

    fun loadLastMessages() {
        viewModelScope.launch {
            delay(500)
            userRepository.getMessages(searchParam, ::setMessages, ::setHomePageError)
        }
    }

    fun fillDestinationInfo(message: HomePageMessage) {
        viewModelScope.launch {
            userRepository.fillDestinationInfo(message, ::setLastMessage, ::setHomePageError)
        }
    }

    fun fillLoggedUser() {
        viewModelScope.launch {
            delay(500)
            userRepository.getUser(::setLoggedUser, ::setError)
        }
    }

    fun updateUserInfo(username: String, jobInfo: String, profile: String? = null, data: Uri? = null) {
        viewModelScope.launch {
            delay(500)
            userRepository.updateUser(username, jobInfo, profile, data, ::setLoggedUser, ::setError)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            delay(500)
            userRepository.signOut(::setLoggedUser, ::setError)
        }
    }

    fun setSearchParam(input: String) {
        searchParam = input
        loadLastMessages()
    }


}