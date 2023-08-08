package ge.spoli.messagingapp.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ge.spoli.messagingapp.domain.MessageRepository
import ge.spoli.messagingapp.presentation.MessagingApp
import ge.spoli.messagingapp.domain.UserRepository
import ge.spoli.messagingapp.model.MessageRepositoryImpl
import ge.spoli.messagingapp.model.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): MessagingApp {
        return app as MessagingApp
    }

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideMessagesRepository(): MessageRepository {
        return MessageRepositoryImpl()
    }
}