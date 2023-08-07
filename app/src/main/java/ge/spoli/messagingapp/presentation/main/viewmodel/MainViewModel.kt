package ge.spoli.messagingapp.presentation.main.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.user.model.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
@HiltViewModel
class MainViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    private var _loggedUser = MutableLiveData<UserEntity?>()
    private var _mainViewError = MutableLiveData<String>()

    val loggedUser: LiveData<UserEntity?> get() = _loggedUser
    val mainViewError: LiveData<String> get() = _mainViewError


    private fun setLoggedUser(user: UserEntity?) {
        _loggedUser.postValue(user)
    }

    private fun setError(error: String) {
        _mainViewError.postValue(error)
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

    fun sign_out() {
        viewModelScope.launch {
            delay(500)
            userRepository.signOut(::setLoggedUser, ::setError)
        }
    }


}