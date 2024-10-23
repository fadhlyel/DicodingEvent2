package com.example.dicodingevent.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.local.entity.Event
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.example.dicodingevent.ui.viewmodel.EventDatabaseViewModel
import com.example.dicodingevent.ui.viewmodel.MainViewModel
import com.example.dicodingevent.ui.viewmodel.ViewModelFactory
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.dataStore

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val mainViewModel by viewModels<MainViewModel>()
    private val binding get() = _binding!!
    private var isFavorite = false
    private var event: Event? = null

    private lateinit var eventDatabaseViewModel: EventDatabaseViewModel

    companion object {
        const val EVENT_DETAIL = "event_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventDatabaseViewModel = obtainViewModel(this@DetailActivity)

        mainViewModel.isLoading.observe(this) {
            toggleLoading(it)
        }

        val eventId = intent.getIntExtra(EVENT_DETAIL, 0)
        mainViewModel.getDetailEvent(eventId)

        eventDatabaseViewModel.getEventById(eventId).observe(this) { eventFromDb ->
            if (eventFromDb != null) {
                event = eventFromDb
                isFavorite = eventFromDb.isFavorite
                updateFavoriteIcon(isFavorite)
            }
        }

        mainViewModel.eventDetail.observe(this) { eventDetail ->
            if (event == null) {
                event = Event(
                    title = eventDetail.name,
                    isFavorite = false,
                    id = eventDetail.id,
                    imageEvent = eventDetail.mediaCover
                )
                eventDatabaseViewModel.insert(event as Event)
            }
            val availableQuota = eventDetail.quota - eventDetail.registrants
            with(binding) {
                tvDetailTitle.text = eventDetail.name
                tvDetailOwn.text = eventDetail.ownerName
                tvDetailQuota.text = availableQuota.toString()
                tvDetailBeginTime.text = eventDetail.beginTime
                tvDetailEndTime.text = eventDetail.endTime
                tvDetailDescription.text = HtmlCompat.fromHtml(
                    eventDetail.description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            }
            Glide.with(this)
                .load(eventDetail.mediaCover)
                .into(binding.imgDetailPhoto)
        }

        binding.btnRegister.setOnClickListener {
            val registerIntent = Intent(Intent.ACTION_VIEW).apply {
                data = mainViewModel.eventDetail.value?.link?.toUri()
            }
            startActivity(registerIntent)
        }

        binding.fabFavorite.setOnClickListener {
            event?.let { currentEvent ->
                currentEvent.isFavorite = !currentEvent.isFavorite
                isFavorite = currentEvent.isFavorite

                eventDatabaseViewModel.update(currentEvent)
                updateFavoriteIcon(isFavorite)

                if (isFavorite) {
                    Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Removed from favorite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): EventDatabaseViewModel {
        val pref = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[EventDatabaseViewModel::class.java]
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_24)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
