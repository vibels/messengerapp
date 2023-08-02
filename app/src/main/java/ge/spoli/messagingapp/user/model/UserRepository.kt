package ge.spoli.messagingapp.user.model


import dagger.Module
import dagger.Provides
import ge.spoli.messagingapp.user.domain.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {
    suspend fun getUserItemsList(): List<UserEntity>
}

@Module
class UserRepositoryProvider() {

    @Provides
    fun provideUserRepositoryInstance(): UserRepository {
        return UserRepositoryImpl()
    }

//    override suspend fun getUserItemsList(): List<UserEntity> {
//        return withContext(Dispatchers.IO){
//            userDao.getUserEntityList()
//        }
//    }
}