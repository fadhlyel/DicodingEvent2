package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingevent.data.local.entity.Event

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Query("SELECT * FROM event WHERE id = :id LIMIT 1")
    fun getEventById(id: Int): LiveData<Event>

    @Query("SELECT * from event WHERE isFavorite = 1")
    fun getFavoriteEvents(): LiveData<List<Event>>
}