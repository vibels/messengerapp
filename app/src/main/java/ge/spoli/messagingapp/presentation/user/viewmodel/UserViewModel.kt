package ge.spoli.messagingapp.presentation.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.spoli.messagingapp.domain.UserRepository
import ge.spoli.messagingapp.domain.user.UserEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    private var _userError = MutableLiveData<String>()
    // One live data, distinguished between update and whole reload by boolean flag, true for update
    private var _users = MutableLiveData<Pair<Boolean, List<UserEntity>>>()
    val userError: LiveData<String> get() = _userError
    val users: LiveData<Pair<Boolean, List<UserEntity>>> get() = _users

    private fun setError(error: String) {
        _userError.postValue(error)
    }

    private fun setUpdateData(users: List<UserEntity>) {
        _users.postValue(Pair(true, users))
    }

    private fun setWholeData(users: List<UserEntity>) {
        _users.postValue(Pair(false, users))
    }

    fun loadUsers(searchParam: String, lastUsername: String? = null) {
        viewModelScope.launch {
            userRepository.loadUsers(searchParam, ::setWholeData, ::setUpdateData, ::setError, lastUsername)
        }
    }
}