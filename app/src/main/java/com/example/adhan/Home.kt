package com.example.adhan

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Home : Fragment() {

    private lateinit var prayerNameTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PrayerAdapter
    private val prayers = mutableListOf<Prayer>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mediaPlayer: MediaPlayer? = null
    private var nextPrayerIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        prayerNameTextView = view.findViewById(R.id.salat_name)
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = PrayerAdapter(prayers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getUserLocation()

        return view
    }

    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(screenOffReceiver)
        stopAdhan()
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    fetchPrayerTimes(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun fetchPrayerTimes(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getPrayerTimes(latitude, longitude)
                Log.d("HomeFragment", "Fetched Prayer Times: ${response.data.timings}")
                val timings = response.data.timings

                val fetchedPrayers = listOf(
                    Prayer("Fajr", timings.Fajr, alarmOn = true),
                    Prayer("Dhuhr", timings.Dhuhr, alarmOn = true),
                    Prayer("Asr", timings.Asr, alarmOn = true),
                    Prayer("Maghrib", timings.Maghrib, alarmOn = true),
                    Prayer("Isha", timings.Isha, alarmOn = true)
                )

                withContext(Dispatchers.Main) {
                    prayers.clear()
                    prayers.addAll(fetchedPrayers)
                    adapter.notifyDataSetChanged()
                    updateNextPrayer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeFragment", "Error fetching prayer times: ${e.message}")
            }
        }
    }

    private fun updateNextPrayer() {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        Log.d("HomeFragment", "Current time: $currentTime")

        nextPrayerIndex = prayers.indexOfFirst { it.time > currentTime }.takeIf { it != -1 } ?: 0
        if (nextPrayerIndex >= 0 && nextPrayerIndex < prayers.size) {
            Log.d("HomeFragment", "Next prayer: ${prayers[nextPrayerIndex].name}")
            prayerNameTextView.text = prayers[nextPrayerIndex].name
        }
    }

    private fun playAdhan() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.adhan)
        mediaPlayer?.start()
    }

    private fun stopAdhan() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                release()
            }
        }
        mediaPlayer = null
    }

    private val screenOffReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                stopAdhan()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAdhan()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
