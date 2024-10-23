package com.example.dicodingevent.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.data.remote.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.ui.adapter.EventsAdapter
import com.example.dicodingevent.ui.viewmodel.MainViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEventUpComing.layoutManager = LinearLayoutManager(context)

        if (mainViewModel.listEventUpComing.value.isNullOrEmpty()) {
            mainViewModel.getDicodingEvents(1)
        }

        mainViewModel.listEventUpComing.observe(viewLifecycleOwner) { upcomingEvents ->
            populateEvents(upcomingEvents)
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            toggleLoading(it)
        }
    }

    private fun populateEvents(events: List<ListEventsItem>) {
        val adapter = EventsAdapter(events)
        binding.rvEventUpComing.adapter = adapter
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBarUpComing.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
