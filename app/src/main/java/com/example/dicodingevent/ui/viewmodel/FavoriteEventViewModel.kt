package com.example.dicodingevent.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.local.entity.Event
import com.example.dicodingevent.data.local.room.EventRepository

class FavoriteEventViewModel(application: Application) : ViewModel() {
    private val mEventRepository: EventRepository = EventRepository(application)

    fun getFavoriteEvents(): LiveData<List<Event>> = mEventRepository.getFavoriteEvents()
}