package ge.spoli.messagingapp.common

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

class Utils {
    companion object {
        fun hideSoftKeyboard(activity: Activity?) {
            if (activity == null) return
            if (activity.currentFocus == null) return
            val inputMethodManager =
                activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}