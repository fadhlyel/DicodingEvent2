package com.example.dicodingevent.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.dicodingevent.databinding.FragmentSettingBinding
import com.example.dicodingevent.ui.viewmodel.SettingViewModel
import com.example.dicodingevent.ui.viewmodel.ViewModelFactory
import com.example.dicodingevent.utils.MyWorker
import com.example.dicodingevent.utils.SettingPreferences
import com.example.dicodingevent.utils.dataStore
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var workManager: WorkManager
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workManager = WorkManager.getInstance(requireContext())

        settingViewModel = obtainViewModel(requireActivity() as AppCompatActivity)

        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        settingViewModel.getNotificationSettings().observe(viewLifecycleOwner) { isNotificationEnabled ->
            if (isNotificationEnabled) {
                binding.switchNotification.isChecked = true
                startPeriodicTask()
            } else {
                binding.switchNotification.isChecked = false
                cancelPeriodicTask()
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSettings(isChecked)
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveNotificationSettings(isChecked)
        }
    }

    private fun startPeriodicTask() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyNotification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelPeriodicTask() {
        workManager.cancelUniqueWork("DailyNotification")
    }

    private fun obtainViewModel(activity: AppCompatActivity): SettingViewModel {
        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory.getInstance(activity.application, pref)
        return ViewModelProvider(activity, factory)[SettingViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}