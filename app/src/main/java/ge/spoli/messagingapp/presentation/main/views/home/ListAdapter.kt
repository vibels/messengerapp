package ge.spoli.messagingapp.presentation.main.views.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.databinding.HomePageListEntryBinding
import ge.spoli.messagingapp.databinding.ListLoadingBinding
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import java.util.concurrent.atomic.AtomicInteger

@SuppressLint("NotifyDataSetChanged")
class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val homePageMessages = mutableListOf<HomePageMessage>()
    private var loading: Boolean = false

    private var populationCounter = AtomicInteger(0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == Constants.LOADED) {
            return ListViewHolder(HomePageListEntryBinding.inflate(inflater, parent, false))
        } else {
            return LoadingViewHolder(
                ListLoadingBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == homePageMessages.size) Constants.LOADING else Constants.LOADED
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < homePageMessages.size) {
            holder as ListViewHolder
            holder.setMessage(homePageMessages[position])
        }
    }

    override fun getItemCount(): Int {
        return homePageMessages.size
    }

    fun setLoading(value: Boolean, notify: Boolean) {
        loading = value
        if (notify) {
            notifyDataSetChanged()
        }
    }

    fun addMessage(message: HomePageMessage) {
        val firstMessageIndex = homePageMessages.indexOfFirst {
            it.id == message.id
        }
        homePageMessages[firstMessageIndex] = message
        if (populationCounter.decrementAndGet() > 0) {
            notifyItemChanged(firstMessageIndex)
        } else run {
            setLoading(value = false, notify = true)
        }
    }

    fun setPopulationCounter(value: Int) {
        populationCounter.set(value)
    }

    fun setMessages(messages: List<HomePageMessage>) {
        homePageMessages.clear()
        if (populationCounter.get() <= 0) {
            setLoading(value = false, notify = false)
        }
        homePageMessages.addAll(messages)
        notifyDataSetChanged()
    }

}