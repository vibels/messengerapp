package ge.spoli.messagingapp.presentation.main.views.home.search

fun interface RequestListener {
    fun onSearchRequest(term: String)
}