package ge.spoli.messagingapp.domain


import ge.spoli.messagingapp.data.UserDao
import ge.spoli.messagingapp.data.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserRepository {
    suspend fun getUserItemsList(): List<UserEntity>
}

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getUserItemsList(): List<UserEntity> {
        return withContext(Dispatchers.IO){
            userDao.getUserEntityList()
        }
    }
}