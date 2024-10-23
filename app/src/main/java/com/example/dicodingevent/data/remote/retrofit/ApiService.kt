package com.example.dicodingevent.data.remote.retrofit

import com.example.dicodingevent.data.remote.response.DetailResponse
import com.example.dicodingevent.data.remote.response.DicodingEventsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<DicodingEventsResponse>

    @GET("events")
    fun getSearchEvents(
        @Query("active") active: Int = 0,
        @Query("q") keyword: String
    ): Call<DicodingEventsResponse>

    @GET("events/{id}")
    fun getDetailEvents(@Path("id") id: Int): Call<DetailResponse>

    @GET("events")
    suspend fun getNearestActiveEvent(
        @Query("active")active: Int = -1,
        @Query("limit")limit: Int = 1
    ) : DicodingEventsResponse
}