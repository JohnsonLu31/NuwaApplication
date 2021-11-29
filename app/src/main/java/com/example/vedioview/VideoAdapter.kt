package com.example.vedioview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.nuwaapplication.R

class VideoAdapter(context: Context, objects: List<Video>) : ArrayAdapter<Video>(context, 0,  objects) {

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_video, null)

        val tvTitle = convertView.findViewById<TextView>(R.id.tv_Title)
        val tvArtist = convertView.findViewById<TextView>(R.id.tv_Artist)

        val video : Video? = getItem(position)
        tvTitle.text = video?.getTitle()
        tvArtist.text = video?.getArtist()

        return convertView
    }
}