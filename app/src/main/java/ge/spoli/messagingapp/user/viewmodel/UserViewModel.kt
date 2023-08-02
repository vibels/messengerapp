package ge.spoli.messagingapp.user.viewmodel

import android.content.Context
import androidx.lifecycle.*
import ge.spoli.messagingapp.user.domain.UserEntity
import ge.spoli.messagingapp.user.model.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel : ViewModel() {

    @Inject
    lateinit var userRepository: UserRepository

    private var _testLiveData = MutableLiveData<List<UserEntity>>()
    val testLiveData: LiveData<List<UserEntity>> get() = _testLiveData

    init {
        viewModelScope.launch {
            val list = userRepository.getUserItemsList()
            _testLiveData.value = list
        }
    }

    fun changeData() {
        // _testLiveData.postValue(DemoEntity(title = "title", description = "desc"))
    }

    companion object {
        fun getViewModelFactory(context: Context): UserViewModelFactory {
            return UserViewModelFactory(context)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel() as T
    }
}