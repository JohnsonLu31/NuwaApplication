package com.example.translator

import android.os.AsyncTask
import org.json.JSONArray
import android.content.ContentValues
import android.util.Log
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

/**
 * Created by hello on 16-Aug-18.
 */
class TranslateAPI(langFrom: String?, langTo: String?, text: String?) {
    var resp: String? = null
    var url: String? = null
    var langFrom: String? = null
    var langTo: String? = null
    var word: String? = null

    internal inner class Async : AsyncTask<String?, String?, String?>() {
        override fun doInBackground(vararg params: String?): String? {
            try {
                url =
                    "https://translate.googleapis.com/translate_a/single?" + "client=gtx&" + "sl=" +
                            langFrom + "&tl=" + langTo + "&dt=t&q=" + URLEncoder.encode(
                        word,
                        "UTF-8"
                    )
                val obj = URL(url)
                val con = obj.openConnection() as HttpURLConnection
                con.setRequestProperty("User-Agent", "Mozilla/5.0")
                val `in` = BufferedReader(InputStreamReader(con.inputStream))
                var inputLine: String?
                val response = StringBuffer()
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                resp = response.toString()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(s: String?) {
            var temp = ""
            if (resp == null) {
                listener!!.onFailure("Network Error")
            } else {
                try {
                    val main = JSONArray(resp)
                    val total = main[0] as JSONArray
                    for (i in 0 until total.length()) {
                        val currentLine = total[i] as JSONArray
                        temp = temp + currentLine[0].toString()
                    }
                    Log.d(ContentValues.TAG, "onPostExecute: $temp")
                    if (temp.length > 2) {
                        listener!!.onSuccess(temp)
                    } else {
                        listener!!.onFailure("Invalid Input String")
                    }
                } catch (e: JSONException) {
                    listener!!.onFailure(e.localizedMessage)
                    e.printStackTrace()
                }
            }
            super.onPostExecute(s)
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onCancelled(s: String?) {
            super.onCancelled(s)
        }

    }

    private var listener: TranslateListener? = null
    fun setTranslateListener(listener: TranslateListener?) {
        this.listener = listener
    }

    interface TranslateListener {
        fun onSuccess(translatedText: String?)
        fun onFailure(ErrorText: String?)
    }

    init {
        this.langFrom = langFrom
        this.langTo = langTo
        word = text
        val async = Async()
        async.execute()
    }
}