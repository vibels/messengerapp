package ge.spoli.messagingapp.presentation

import android.content.Context
import androidx.lifecycle.*
import ge.spoli.messagingapp.data.UserDatabase
import ge.spoli.messagingapp.data.UserEntity
import ge.spoli.messagingapp.domain.UserRepository
import ge.spoli.messagingapp.domain.UserRepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

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
        fun getViewModelFactory(context: Context): MainViewModelFactory {
            return MainViewModelFactory(context)
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(
            UserRepositoryImpl(
                UserDatabase.getInstance(context).getUserDao()
            )
        ) as T
    }
}