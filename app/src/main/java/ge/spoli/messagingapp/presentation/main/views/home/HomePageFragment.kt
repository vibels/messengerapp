package ge.spoli.messagingapp.presentation.main.views.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ge.spoli.messagingapp.databinding.FragmentHomePageBinding
import ge.spoli.messagingapp.domain.chat.HomePageMessage
import ge.spoli.messagingapp.presentation.chat.viewmodels.ChatViewModel

@AndroidEntryPoint
class HomePageFragment :  Fragment() {
    val viewModel: ChatViewModel by activityViewModels()
    private lateinit var binding: FragmentHomePageBinding
    private val listAdapter = ListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomePageBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.homePageMessages.observe(viewLifecycleOwner) {
            setLoading(false)
            homePageMessagesLoaded(it)
        }
        viewModel.homePageError.observe(viewLifecycleOwner) {
            setLoading(false)
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        viewModel.lastMessage.observe(viewLifecycleOwner) {
            setLoading(false)
            messageReceived(it)
        }
        setup()
        load()
    }
    
    private fun setup() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = listAdapter

        binding.search.setRequestListener {
            setLoading(true)
            listAdapter.setLoading(value = true, notify = false)
            viewModel.setSearchParam(it)
        }
    }

    private fun load() {
        setLoading(true)
        listAdapter.setLoading(value = true, notify = true)
        viewModel.loadLastMessages()
    }

    private fun setLoading(value: Boolean) {
        binding.notFound.visibility = View.GONE
        if (value) {
            binding.homeProgressBar.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.homeProgressBar.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun homePageMessagesLoaded(messages: List<HomePageMessage>) {
        if (messages.isEmpty()) {
            binding.notFound.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.list.visibility = View.VISIBLE
            listAdapter.setMessages(messages.sortedByDescending { it.date })
            listAdapter.setPopulationCounter(messages.count { it.username == null })
            for (message in messages) {
                if (message.username == null) {
                    viewModel.fillDestinationInfo(message)
                }
            }
        }
    }

    private fun messageReceived(message: HomePageMessage) {
        listAdapter.addMessage(message)
    }





}