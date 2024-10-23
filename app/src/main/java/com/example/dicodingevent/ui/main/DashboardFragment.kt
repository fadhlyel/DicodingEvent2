package com.example.dicodingevent.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingevent.data.remote.response.ListEventsItem
import com.example.dicodingevent.databinding.FragmentDashboardBinding
import com.example.dicodingevent.ui.adapter.EventsAdapter
import com.example.dicodingevent.ui.viewmodel.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private val mainViewModel by viewModels<MainViewModel>()
    private var searchJob: Job? = null
    private lateinit var eventsAdapter: EventsAdapter
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerView()

        if (mainViewModel.listEventFinished.value.isNullOrEmpty()) {
            mainViewModel.getDicodingEvents(0)
        }

        mainViewModel.listEventFinished.observe(viewLifecycleOwner) { finishedEvents ->
            displayEvents(finishedEvents)
        }

        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResult ->
            if (!searchResult.isNullOrEmpty()) {
                displayEvents(searchResult)
            } else {
                mainViewModel.listEventFinished.value?.let { displayEvents(it) }
            }
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            toggleLoading(it)
        }

        setupSearchFunctionality()
    }

    private fun initializeRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
        binding.rvEventFinished.layoutManager = layoutManager
        eventsAdapter = EventsAdapter(emptyList())
        binding.rvEventFinished.adapter = eventsAdapter
    }

    private fun setupSearchFunctionality() {
        binding.svSearchEvent.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.svSearchEvent.clearFocus()
                if (!query.isNullOrEmpty()) {
                    mainViewModel.getSearchEvents(query)
                } else {
                    resetToDefaultSearch()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(500)
                    if (!newText.isNullOrEmpty()) {
                        mainViewModel.getSearchEvents(newText)
                    } else {
                        resetToDefaultSearch()
                    }
                }
                return true
            }
        })
    }

    private fun resetToDefaultSearch() {
        mainViewModel.resetSearchResult()
        mainViewModel.listEventFinished.value?.let { displayEvents(it) }
    }

    private fun displayEvents(eventsList: List<ListEventsItem>) {
        eventsAdapter.updateData(eventsList)

        if (eventsList.isEmpty()) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvEventFinished.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.rvEventFinished.visibility = View.VISIBLE
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBarFinished.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
