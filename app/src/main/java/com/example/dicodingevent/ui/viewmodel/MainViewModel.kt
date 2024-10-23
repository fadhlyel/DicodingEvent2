package com.example.dicodingevent.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.remote.response.DetailResponse
import com.example.dicodingevent.data.remote.response.DicodingEventsResponse
import com.example.dicodingevent.data.remote.response.Event
import com.example.dicodingevent.data.remote.response.ListEventsItem
import com.example.dicodingevent.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _listEventUpComing = MutableLiveData<List<ListEventsItem>>()
    val listEventUpComing: LiveData<List<ListEventsItem>> = _listEventUpComing

    private val _listEventFinished = MutableLiveData<List<ListEventsItem>>()
    val listEventFinished: LiveData<List<ListEventsItem>> = _listEventFinished

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    private val _eventDetail = MutableLiveData<Event>()
    val eventDetail: LiveData<Event> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun getDicodingEvents(status: Int) {
        _isLoading.value= true
        val client = ApiConfig.getApiService().getEvents(status)
        client.enqueue(object : Callback<DicodingEventsResponse> {
            override fun onResponse(
                call: Call<DicodingEventsResponse>,
                response: Response<DicodingEventsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        when (status) {
                            0 -> _listEventFinished.value = response.body()?.listEvents
                            1 -> _listEventUpComing.value = response.body()?.listEvents
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DicodingEventsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getDetailEvent(id: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvents(id)
        client.enqueue(object: Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    _eventDetail.value = responseBody?.event
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getSearchEvents(keyword: String) {
        _isLoading.value= true
        val client = ApiConfig.getApiService().getSearchEvents(0, keyword)
        client.enqueue(object: Callback<DicodingEventsResponse> {
            override fun onResponse(
                call: Call<DicodingEventsResponse>,
                response: Response<DicodingEventsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _searchResults.value = response.body()?.listEvents
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DicodingEventsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun resetSearchResult() {
        _searchResults.value = emptyList()
    }
}