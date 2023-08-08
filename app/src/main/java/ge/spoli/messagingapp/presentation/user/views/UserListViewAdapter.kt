package ge.spoli.messagingapp.presentation.user.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ge.spoli.messagingapp.common.Constants
import ge.spoli.messagingapp.databinding.ListLoadingBinding
import ge.spoli.messagingapp.databinding.UserListEntryBinding
import ge.spoli.messagingapp.domain.user.UserEntity
import ge.spoli.messagingapp.presentation.user.views.viewholders.UserListEntryViewHolder
import ge.spoli.messagingapp.presentation.user.views.viewholders.UserListLoadingViewHolder

class UserListViewAdapter(private var userActivity: UserActivity): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val users = mutableListOf<UserEntity>()
    var loading: Boolean = false
    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == Constants.LOADING) {
            UserListLoadingViewHolder(ListLoadingBinding.inflate(inflater, parent, false))
        } else {
            UserListEntryViewHolder(UserListEntryBinding.inflate(inflater, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position == users.size) {
            Constants.LOADING
        } else {
            Constants.LOADED
        }

    override fun getItemCount(): Int {
        var size = users.size
        if (loading) size = ++size
        return size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!loading && position < users.size) {
            holder as UserListEntryViewHolder
            holder.update(users[position], userActivity)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateLoading() {
        if(!loading) {
            loading = true
            notifyDataSetChanged()
        }
    }

    fun update(updatedUsers: List<UserEntity>) {
        users.clear()
        append(updatedUsers)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun append(updatedUsers: List<UserEntity>) {
        loading = false
        users.addAll(updatedUsers)
        notifyDataSetChanged()
    }
}