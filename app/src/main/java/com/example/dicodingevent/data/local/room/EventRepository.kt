package com.example.dicodingevent.data.local.room

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.dicodingevent.data.local.entity.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventRepository(application: Application) {
    private val mEventDao: EventDao = EventRoomDatabase.getDatabase(application).eventDao()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    fun insert(event: Event) {
        coroutineScope.launch {
            mEventDao.insert(event)
        }
    }

    fun update(event: Event) {
        coroutineScope.launch {
            mEventDao.update(event)
        }
    }

    fun getFavoriteEvents(): LiveData<List<Event>> = mEventDao.getFavoriteEvents()

    fun getEventById(id: Int): LiveData<Event> {
        return mEventDao.getEventById(id)
    }
}