package com.example.musicplayer

import java.io.Serializable

class Song(private var title: String, private var artist: String, private var path: String) : Serializable{

    fun getTitle() : String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getArtist() : String {
        return artist
    }

    fun setArtist(artist: String) {
        this.artist = artist
    }

    fun getPath() : String {
        return path
    }

    fun setPath(path: String) {
        this.path = path
    }


}