package ge.spoli.messagingapp.main.viewmodel

import android.content.Context
import androidx.lifecycle.*
import ge.spoli.messagingapp.user.domain.UserEntity
import ge.spoli.messagingapp.user.model.UserRepository
import ge.spoli.messagingapp.user.model.UserRepositoryImpl
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel : ViewModel() {

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
        fun getViewModelFactory(context: Context): MainViewModelFactory {
            return MainViewModelFactory(context)
        }
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}