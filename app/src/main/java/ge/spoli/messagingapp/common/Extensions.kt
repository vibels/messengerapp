package ge.spoli.messagingapp.common

import android.text.format.DateFormat
import java.util.*


fun Long.convertTimestamp(): String {
    val now = System.currentTimeMillis()
    val timePassedInSec = now - this
    return if(timePassedInSec < 60000) {
        "1 min"
    } else if(timePassedInSec < 60 * 60000) {
        val minutes = timePassedInSec / 60000
        "$minutes min"
    } else if( timePassedInSec < 60 * 60 * 24000) {
        val hrs = timePassedInSec / (60 * 60000)
        "$hrs hours"
    } else {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = this
        DateFormat.format("dd MMM",calendar).toString()
    }
}