package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.nuwaapplication.R
import com.koushikdutta.ion.Ion
import java.text.SimpleDateFormat
import java.util.*

class DailyWeatherAdapter(context: Context, weatherlist: List<Weather>): ArrayAdapter<Weather>(context, 0, weatherlist) {

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = LayoutInflater.from(context).inflate(R.layout.item_weather, parent, false)

        val tvDate = convertView.findViewById<TextView>(R.id.tvDate)
        val tvTemp = convertView.findViewById<TextView>(R.id.tvTemp)
        val iconWeather = convertView.findViewById<ImageView>(R.id.iconWeather)

        val weather: Weather? = getItem(position)
        tvTemp.text = "${weather?.getTemp()} ℃"

        Ion.with(context)
            .load("http://openweathermap.org/img/w/${weather?.getIcon()}.png")
            .intoImageView(iconWeather)

        val date = Date(weather?.getDate()?.times(1000) as Long)
        val dateFormat = SimpleDateFormat("EEE MMM dd", Locale.ENGLISH)
        dateFormat.timeZone = TimeZone.getTimeZone(weather.getTimeZone())
        tvDate.text = dateFormat.format(date)

        return convertView
    }
}