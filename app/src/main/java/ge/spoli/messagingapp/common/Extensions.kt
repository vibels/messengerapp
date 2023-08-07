package ge.spoli.messagingapp.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.convertTimestamp(): String {
    val formatter: SimpleDateFormat
    val now = System.currentTimeMillis()
    val passed = (now - this)
    if (passed < 60000) {
        return "1 min"
    } else if(passed < 60 * 60 * 1000) {
        formatter = SimpleDateFormat("m min")
    } else if(passed < 60 * 60 * 24 * 1000) {
        formatter = SimpleDateFormat("k hours")
    } else {
        formatter = SimpleDateFormat("dd MMM")
    }

    return formatter.format(Date(this))
}