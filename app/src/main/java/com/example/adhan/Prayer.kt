package com.example.adhan

data class Prayer(val name: String, val time: String,var alarmOn:Boolean)

data class PrayerTimesResponse(val data: PrayerData)

data class PrayerData(val timings: Timings)

data class Timings(
    val Fajr: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String
)