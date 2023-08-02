package ge.spoli.messagingapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {

    @Query("Select * from UserEntity")
    fun getUserEntityList(): List<UserEntity>

    @Insert
    fun addUserEntity(userEntity: UserEntity)
}