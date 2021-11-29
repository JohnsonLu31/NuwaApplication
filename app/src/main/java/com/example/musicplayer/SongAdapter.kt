package com.example.musicplayer


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.nuwaapplication.R

class SongAdapter(context: Context, objects: List<Song>) : ArrayAdapter<Song>(context, 0,  objects) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_song, null)

        val tvTitle = convertView.findViewById<TextView>(R.id.tv_Title)
        val tvArtist = convertView.findViewById<TextView>(R.id.tv_Artist)

        val song : Song? = getItem(position)
        tvTitle.text = song?.getTitle()
        tvArtist.text = song?.getArtist()



        return convertView
    }
}