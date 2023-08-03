package ge.spoli.messagingapp.presentation.main.viewmodel

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

@HiltViewModel
class MainViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    private var _loggedUser = MutableLiveData<UserEntity>()
    private var _mainViewError = MutableLiveData<String>()

    val loggedUser: LiveData<UserEntity> get() = _loggedUser
    val mainViewError: LiveData<String> get() = _mainViewError


    private fun setLoggedUser(user: UserEntity) {
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

    fun updateUserInfo(username: String, jobInfo: String, profile: String) {
        viewModelScope.launch {
            userRepository.updateUser(username, jobInfo, profile, ::setLoggedUser, ::setError)
        }
    }


}