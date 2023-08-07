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
import ge.spoli.messagingapp.presentation.main.viewmodel.MainViewModel

@AndroidEntryPoint
class HomePageFragment :  Fragment() {
    val mainViewModel: MainViewModel by activityViewModels()
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
        mainViewModel.messages.observe(viewLifecycleOwner) {
            homePageMessagesLoaded(it)
        }
        mainViewModel.homePageError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showError(it)
            }
        }
        mainViewModel.lastMessage.observe(viewLifecycleOwner) {
            messageReceived(it)
        }
        setup()
        load()
    }
    
    private fun setup() {
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = listAdapter

        binding.search.setRequestListener {
            mainViewModel.setSearchParam(it)
        }
    }

    private fun load() {
        listAdapter.setLoading(value = true, notify = true)
        mainViewModel.loadLastMessages()
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun homePageMessagesLoaded(messages: List<HomePageMessage>) {
        listAdapter.setMessages(messages.sortedByDescending { it.date })
        listAdapter.setPopulationCounter(messages.count { it.username == null })
        for (message in messages) {
            if (message.username == null) {
                mainViewModel.fillDestinationInfo(message)
            }
        }
    }

    private fun messageReceived(message: HomePageMessage) {
        listAdapter.addMessage(message)
    }





}