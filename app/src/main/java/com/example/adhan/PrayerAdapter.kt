package com.example.adhan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PrayerAdapter(private val prayers: List<Prayer>) : RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder>() {

    inner class PrayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val salatName: TextView = view.findViewById(R.id.salat_name)
        val salatTime: TextView = view.findViewById(R.id.salat_time)
        val alarmIcon: ImageView = view.findViewById(R.id.alarmicon)

        fun bind(prayer: Prayer) {
            salatName.text = prayer.name
            salatTime.text = prayer.time
            updateAlarmIcon(prayer.alarmOn)


            alarmIcon.setOnClickListener {
                prayer.alarmOn = !prayer.alarmOn
                updateAlarmIcon(prayer.alarmOn)
            }
        }

        private fun updateAlarmIcon(isAlarmOn: Boolean) {
            if (isAlarmOn) {
                alarmIcon.setImageResource(R.drawable.alarmon)
            } else {
                alarmIcon.setImageResource(R.drawable.alarmoff)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.salat_row, parent, false)
        return PrayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        holder.bind(prayers[position])
    }

    override fun getItemCount(): Int = prayers.size
}