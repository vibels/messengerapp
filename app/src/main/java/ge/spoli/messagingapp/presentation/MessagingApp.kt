package ge.spoli.messagingapp.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}
@HiltAndroidApp
class MessagingApp : Application(), ExceptionListener {
    override fun onCreate() {
        super.onCreate()
        setupExceptionHandler()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        Log.d("Messaging App", throwable.message ?: "")
    }

    private fun setupExceptionHandler(){
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    uncaughtException(Looper.getMainLooper().thread, e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            uncaughtException(t, e)
        }
    }
}