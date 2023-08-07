package ge.spoli.messagingapp.presentation.entrypoint.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import ge.spoli.messagingapp.presentation.entrypoint.views.EntrypointActivity
import ge.spoli.messagingapp.presentation.user.model.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(val userRepository: UserRepository): ViewModel() {
    private var _entryId = MutableLiveData<String>()
    private var _entrypointError = MutableLiveData<String>()
    val entryId: LiveData<String> get() = _entryId
    val entrypointError: LiveData<String> get() = _entrypointError

    private fun setEntryId(id: String) {
        _entryId.postValue(id)
    }

    private fun setError(error: String) {
        _entrypointError.postValue(error)
    }

    private fun getUser(id: String) {
        viewModelScope.launch {
            delay(500)
            userRepository.fetchUser(id, ::setEntryId, ::setError)
        }
    }

    private fun save(id: String, username: String, jobInfo: String) {
        viewModelScope.launch {
            userRepository.save(id, username, jobInfo, ::setError)
            getUser(id)
        }
    }

    fun saveUserInfo(
        username: String,
        password: String,
        jobInfo: String,
        activity: EntrypointActivity
    ) {
        Firebase.auth.createUserWithEmailAndPassword("$username@dummy.email", password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Firebase.auth.currentUser?.uid?.let {
                        save(
                            it,
                            username,
                            jobInfo,
                        )
                    } ?: run {
                        setError(task.exception?.message ?: "Something went wrong")
                    }
                } else {
                    setError(
                        task.exception?.message ?: "Something went wrong"
                    )
                }
            }
    }

    fun login(username: String, password: String, activity: EntrypointActivity) {
            Firebase.auth.signInWithEmailAndPassword("$username@dummy.email", password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Firebase.auth.currentUser?.uid?.let {
                            getUser(it)
                        }
                    } else {
                        setError(task.exception?.message ?: "Something went wrong")
                    }
                }
    }

    fun loginWithId(id: String) {
        getUser(id)
    }


}