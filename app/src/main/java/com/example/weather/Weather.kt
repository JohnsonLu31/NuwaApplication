package com.example.weather

class Weather (private var date: Long, private var timeZone: String, private var temp: Double, private var icon: String) {

    fun getDate(): Long {
        return date
    }

    fun setDate(date: Long) {
        this.date = date
    }

    fun getTimeZone(): String {
        return timeZone
    }

    fun setTimeZone(timeZone: String) {
        this.timeZone = timeZone
    }

    fun getTemp(): Double {
        return temp
    }

    fun setTemp(temp: Double) {
        this.temp = temp
    }

    fun getIcon(): String {
        return icon
    }

    fun setIcon(icon: String) {
        this.icon = icon
    }
}