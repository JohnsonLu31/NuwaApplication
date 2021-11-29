package com.example.nuwaapplication.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmClock.AlarmClockMain
import com.example.chatBox.data.Message
import com.example.musicplayer.ListMusicActivity
import com.example.nuwaapplication.util.BotResponse
import com.example.nuwaapplication.util.Constants.OPEN_GOOGLE
import com.example.nuwaapplication.util.Constants.OPEN_SEARCH
import com.example.nuwaapplication.util.Constants.RECEIVE_ID
import com.example.nuwaapplication.util.Constants.SEND_ID
import com.example.nuwaapplication.util.Time
import com.example.nuwaapplication.R
import com.example.vedioview.ListVideoActivity
import com.example.weather.WeatherMain
import com.example.youtubevideo.YoututbevideoMainActivity
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.*
import kotlinx.android.synthetic.main.chatbox_main.*
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class ChatBoxActivity : AppCompatActivity() {


    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    private lateinit var messagingadapter: MessagingAdapter
    private val botlist = listOf("小美", "小華", "阿明", "阿忠")

    val random = (0..3).random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbox_main)

        //NUWA SETTING------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //-------------------------------------------------------

        recycleView()

        clickEvents()

    }

    private fun clickEvents() {
        btn_send.setOnClickListener {
            sendMessage()
        }

        et_message.setOnClickListener {

            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
                }
            }
        }
    }

    private fun recycleView() {
        messagingadapter = MessagingAdapter()
        rv_messages.adapter = messagingadapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            et_message.setText("")

            messagingadapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(messagingadapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun sendMessageByVoice(message: String) {
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            GlobalScope.launch {
                delay(1000)

                withContext(Dispatchers.Main) {
                    messagingadapter.insertMessage(Message(message, SEND_ID, timeStamp))
                    rv_messages.scrollToPosition(messagingadapter.itemCount - 1)

                    botResponse(message)
                }
            }
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            delay(1000)

            withContext(Dispatchers.Main) {
                val response = BotResponse.basicResponse(message)

                messagingadapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))
                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)

                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String = message.substringAfter("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }
                    else -> {
                        //NUWA TTS
                        mRobotAPI.startTTS(response)
                    }
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
            }
        }
    }

    private fun customMessage(messsage: String) {
        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagingadapter.insertMessage(Message(messsage, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(messagingadapter.itemCount - 1)
            }
        }
    }

    //NUWA

    override fun onDestroy() {
        super.onDestroy()
        mRobotAPI.release()
    }


    val robotEventListener = object : RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)

            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)
            //Nuwa TTS

            mRobotAPI.motionPlay("666_PE_Wave", true)
            mRobotAPI.startTTS("你好! 我是 ${botlist[random]}, 請問有甚麼需要我為您服務的嗎?")
            customMessage("你好! 我是 ${botlist[random]}, 請問有甚麼需要我為您服務的嗎?")
        }

        override fun onWikiServiceStop() {

        }

        override fun onWikiServiceCrash() {

        }

        override fun onWikiServiceRecovery() {

        }

        override fun onStartOfMotionPlay(p0: String?) {
            Log.d("test", "onStartOfMotionPlay $p0")
        }

        override fun onPauseOfMotionPlay(p0: String?) {

        }

        override fun onStopOfMotionPlay(p0: String?) {
            Log.d("Test", "onStopMotionPlay $p0")
        }

        override fun onCompleteOfMotionPlay(p0: String?) {
            Log.d("Test", "onCompleteOfMotionPlay $p0")
            mRobotAPI.hideWindow(true)
        }

        override fun onPlayBackOfMotionPlay(p0: String?) {

        }

        override fun onErrorOfMotionPlay(p0: Int) {

        }

        override fun onPrepareMotion(p0: Boolean, p1: String?, p2: Float) {

        }

        override fun onCameraOfMotionPlay(p0: String?) {

        }

        override fun onGetCameraPose(
                p0: Float,
                p1: Float,
                p2: Float,
                p3: Float,
                p4: Float,
                p5: Float,
                p6: Float,
                p7: Float,
                p8: Float,
                p9: Float,
                p10: Float,
                p11: Float
        ) {

        }

        override fun onTouchEvent(p0: Int, p1: Int) {
            Log.d("Test", "onTouchEvent type=$p0, touch=$p1")
        }

        override fun onPIREvent(p0: Int) {
        }

        override fun onTap(p0: Int) {
            Log.d("Test", "onTap type=$p0")
        }

        override fun onLongPress(p0: Int) {
            Log.d("Test", "onLongPress type=$p0")
            when (p0) {
                1 -> startActivity(Intent(applicationContext, WeatherMain::class.java))
                2 -> mRobotAPI.startSpeech2Text(false)
                3 -> startActivity(Intent(applicationContext, AlarmClockMain::class.java))
                4 -> startActivity(Intent(applicationContext, ListMusicActivity::class.java))
                5 -> startActivity(Intent(applicationContext, ListVideoActivity::class.java))
                6 -> startActivity(Intent(applicationContext, YoututbevideoMainActivity::class.java))
            }
        }

        override fun onWindowSurfaceReady() {

        }

        override fun onWindowSurfaceDestroy() {

        }

        override fun onTouchEyes(p0: Int, p1: Int) {

        }

        override fun onRawTouch(p0: Int, p1: Int, p2: Int) {

        }

        override fun onFaceSpeaker(p0: Float) {

        }

        override fun onActionEvent(p0: Int, p1: Int) {

        }

        override fun onDropSensorEvent(p0: Int) {

        }

        override fun onMotorErrorEvent(p0: Int, p1: Int) {

        }
    }


    val voiceEventListener = object : VoiceEventListener {
        override fun onWakeup(p0: Boolean, p1: String?, p2: Float) {

        }

        override fun onTTSComplete(p0: Boolean) {
            Log.d("Test", "TTS $p0")
            //mRobotAPI.startSpeech2Text(false)

        }


        override fun onSpeechRecognizeComplete(p0: Boolean, p1: VoiceEventListener.ResultType?, p2: String?) {
            Log.d("Test", "onSpeechRecognizeComplete Boolean $p0, Type $p1, String $p2")
        }

        override fun onSpeech2TextComplete(p0: Boolean, p1: String?) {
            val result_string = VoiceResultJsonParser.parseVoiceResult(p1)

            when (BotResponse.basicResponse(result_string)) {
                "weather" -> startActivity(Intent(this@ChatBoxActivity, WeatherMain::class.java))
                "alarm" -> startActivity(Intent(this@ChatBoxActivity, AlarmClockMain::class.java))
                "music" -> startActivity(Intent(this@ChatBoxActivity, ListMusicActivity::class.java))
                "video" -> startActivity(Intent(this@ChatBoxActivity, ListVideoActivity::class.java))
                "youtube" -> startActivity(Intent(this@ChatBoxActivity, YoututbevideoMainActivity::class.java))
                else -> {
                    sendMessageByVoice(result_string)
                    mRobotAPI.motionPlay(BotResponse.basicMove(result_string), true)
                }
            }
        }

        override fun onMixUnderstandComplete(p0: Boolean, p1: VoiceEventListener.ResultType?, p2: String?) {

        }

        override fun onSpeechState(
                p0: VoiceEventListener.ListenType?,
                p1: VoiceEventListener.SpeechState?
        ) {
            Log.d("Test", "Speech Type $p0, State $p1")
        }

        override fun onSpeakState(p0: VoiceEventListener.SpeakType?,p1: VoiceEventListener.SpeakState?) {
            Log.d("Test", "Speak Type $p0, State $p1")
        }

        override fun onGrammarState(p0: Boolean, p1: String?) {
            Log.d("Test", "Boolean: $p0, String: $p1")
        }

        override fun onListenVolumeChanged(p0: VoiceEventListener.ListenType?, p1: Int) {

        }

        override fun onHotwordChange(
                p0: VoiceEventListener.HotwordState?,
                p1: VoiceEventListener.HotwordType?,
                p2: String?
        ) {

        }
    }
}