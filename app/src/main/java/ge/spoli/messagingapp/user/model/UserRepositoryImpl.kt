package ge.spoli.messagingapp.user.model

import ge.spoli.messagingapp.user.domain.UserEntity

class UserRepositoryImpl : UserRepository {
    override suspend fun getUserItemsList(): List<UserEntity> {
        TODO("Not yet implemented")
    }
}