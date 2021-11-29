package com.example.nuwaapplication.util

import com.example.nuwaapplication.util.Constants.OPEN_GOOGLE
import com.example.nuwaapplication.util.Constants.OPEN_SEARCH
import java.lang.Exception

object BotResponse {

    fun basicResponse(_message: String): String {


        val random = (0..2).random()
        val message = _message.toLowerCase()

        return when{

            //Hello
            message.contains("你好") || message.contains("哈囉") || message.contains("嗨") -> {
                when (random) {
                    0 -> "你好啊"
                    1 -> "今天過得如何啊?"
                    2 -> "哈囉"
                    else -> "error"
                }
            }

            //How are you
            message.contains("你好嗎") || message.contains("今天過得怎麼樣") -> {
                when (random) {
                    0 -> "我今天很好，謝謝你的關心!"
                    1 -> "我有點餓"
                    2 -> "我很好!那你呢?"
                    else -> "error"
                }
            }

            message.contains("再見") || message.contains("掰掰")  || message.contains("拜拜") -> {
                when (random) {
                    0 -> "下次再來找我喔!"
                    1 -> "明天在一起玩吧"
                    2 -> "再見"
                    else -> "error"
                }
            }

            message.contains("正") && message.contains("反") -> {
                val r = (0..1).random()
                val result = if (r == 0) "正面" else "反面"

                result
            }

            //Solve maths
            message.contains("solve") -> {
                val equation: String = message.substringAfter("solve")

                return try{
                    val answer = SolveMath.solveMath(equation ?: "0")
                    answer.toString()

                }catch (e: Exception){
                    "Sorry, I can't solve that!"
                }
            }

            //Gets the current time
            message.contains("時間") -> {
                Time.timeStamp()
            }

            //Opens Google
            message.contains("打開") && message.contains("google") -> {
                OPEN_GOOGLE
            }

            //Opens Search
            message.contains("搜尋") -> {
                OPEN_SEARCH
            }

            message.contains("天氣") -> {
                "weather"
            }
            message.contains("鬧鐘") -> {
                "alarm"
            }
            message.contains("音樂") -> {
                "music"
            }
            message.contains("影片") -> {
                "video"
            }
            message.contains("線上") -> {
                "youtube"
            }


            else -> {
                when (random) {
                    0 -> "我不了解你在說什麼"
                    1 -> "我不知道"
                    2 -> "試著問我其他東西"
                    else -> "error"
                }
            }
        }
    }

    fun basicMove(_message: String) : String {

        val message = _message.toLowerCase()

        return when{

            //Hello
            message.contains("你好") || message.contains("哈囉") || message.contains("嗨") -> {
                "666_PE_Wave"
            }

            //How are you
            message.contains("你好嗎") || message.contains("今天過得怎麼樣") -> {
                "666_RE_Ask"
            }

            message.contains("再見") || message.contains("掰掰") || message.contains("拜拜")-> {
                "666_RE_Bye"
            }

            message.contains("正") && message.contains("反") -> {
                "666_RE_Change"
            }

            //Solve maths
            message.contains("solve") -> {
                "666_PE_Sorcery04"
            }

            //Gets the current time
            message.contains("時間") -> {
                "666_PE_Triangel"
            }

            //Opens Google
            message.contains("打開") && message.contains("google") -> {
                "666_RE_Embrace"
            }

            //Opens Search
            message.contains("搜尋") -> {
                "666_RE_Encourage"
            }


            else -> {
                "666_PE_Ultraman"
            }
        }

    }
}