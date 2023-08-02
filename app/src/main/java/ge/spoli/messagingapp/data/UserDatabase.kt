package ge.spoli.messagingapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                if (instance == null) {
                    instance = Room
                        .databaseBuilder(context = context, UserDatabase::class.java, "db-name")
                        .build()
                }
            }
            return instance!!
        }
    }
}