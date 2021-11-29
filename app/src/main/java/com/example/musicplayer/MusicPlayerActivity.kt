package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import java.io.IOException
import kotlin.concurrent.thread
import com.example.nuwaapplication.R
import com.example.nuwaapplication.ui.ChatBoxActivity
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.alarm_main.*
import kotlinx.android.synthetic.main.music_activity.*


class MusicPlayerActivity : AppCompatActivity() {

    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    lateinit var musicPlayer: MediaPlayer
    lateinit var song : Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_activity)

        //NUWA SETTING------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //------------------------------------------------------


        song = intent.getSerializableExtra("song") as Song

        tv_Title.text = song.getTitle()
        tv_Artist.text = song.getArtist()

        musicload()

        musicPlayer.start()
        btn_Play.setBackgroundResource(R.drawable.ic_baseline_pause_24)

        playmusic()

        volumecontrol()

        progresscontrol()

        thread {
            while (true) {
                if (musicPlayer.isPlaying) {
                    try {
                        runOnUiThread {
                            mRobotAPI.motionPlay("666_PE_Singing", true)
                            seekBar_Time.progress = musicPlayer.currentPosition
                            tv_Time.text = millisecondsToString(musicPlayer.currentPosition)
                        }
                        Thread.sleep(1000)
                        Log.d("The progress", Thread.currentThread().name)
                    } catch (e: Exception) {
                        seekBar_Time.progress = 0
                    }
                }
            }
        }

    }

    private fun progresscontrol() {

        val duration = millisecondsToString(musicPlayer.duration)
        tv_Duration.text = duration

        seekBar_Time.max = musicPlayer.duration
        seekBar_Time.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicPlayer.seekTo(progress)
                    seekBar?.progress = progress
                    Log.d("Progress", Thread.currentThread().name)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

        })

    }

    private fun millisecondsToString(time : Int): String {
        var elapsedTime = ""
        val mins = time / 1000 / 60
        val seconds = time / 1000 % 60

        elapsedTime = "$mins:"

        if (seconds < 10) {
            elapsedTime += "0"
        }
        elapsedTime += seconds

        return elapsedTime
    }


    private fun volumecontrol() {
        seekBar_Volume.progress = 50
        seekBar_Volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                musicPlayer.setVolume(volume, volume)
                Log.d("Volume", Thread.currentThread().name)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO("Not yet implemented")
            }
        })
    }


    private fun playmusic() {
        btn_Play.setOnClickListener {
            if(musicPlayer.isPlaying) {
                musicPlayer.pause()
                btn_Play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                Log.d("Stop", Thread.currentThread().name)
            } else {
                musicPlayer.start()
                btn_Play.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                Log.d("Start", Thread.currentThread().name)

            }

        }
    }

    private fun musicload() {
        musicPlayer = MediaPlayer()
        try {
            musicPlayer.setDataSource(song.getPath())
            musicPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        musicPlayer.isLooping = true
        musicPlayer.seekTo(0)
        musicPlayer.setVolume(0.5f, 0.5f)
        Log.d("Main_Activity", Thread.currentThread().name)
    }

    override fun onDestroy() {
        musicPlayer.stop()
        Log.d("Test", "Stop successfully")
        super.onDestroy()
    }

    val robotEventListener = object : RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)

        }

        override fun onWikiServiceStop() {}
        override fun onWikiServiceCrash() {}
        override fun onWikiServiceRecovery() {}
        override fun onStartOfMotionPlay(p0: String?) {}
        override fun onPauseOfMotionPlay(p0: String?) {}
        override fun onStopOfMotionPlay(p0: String?) {}
        override fun onCompleteOfMotionPlay(p0: String?) {
            mRobotAPI.hideWindow(true)
        }
        override fun onPlayBackOfMotionPlay(p0: String?) {}
        override fun onErrorOfMotionPlay(p0: Int) {}
        override fun onPrepareMotion(p0: Boolean, p1: String?, p2: Float) {}
        override fun onCameraOfMotionPlay(p0: String?) {}
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
        ) {}
        override fun onTouchEvent(p0: Int, p1: Int) {}
        override fun onPIREvent(p0: Int) {}
        override fun onTap(p0: Int) {}
        override fun onLongPress(p0: Int) {
            when (p0) {
                1 -> {
                    musicPlayer.stop()
                    startActivity(Intent(applicationContext, ListMusicActivity::class.java))
                }
                2 -> mRobotAPI.startSpeech2Text(false)
            }
            Log.d("ListMusic Test", "on Long Press $p0")
        }
        override fun onWindowSurfaceReady() {}
        override fun onWindowSurfaceDestroy() {}
        override fun onTouchEyes(p0: Int, p1: Int) {}
        override fun onRawTouch(p0: Int, p1: Int, p2: Int) {}
        override fun onFaceSpeaker(p0: Float) {}
        override fun onActionEvent(p0: Int, p1: Int) {}
        override fun onDropSensorEvent(p0: Int) {}
        override fun onMotorErrorEvent(p0: Int, p1: Int) {}
    }

    val voiceEventListener = object : VoiceEventListener {
        override fun onWakeup(p0: Boolean, p1: String?, p2: Float) {}

        override fun onTTSComplete(p0: Boolean) {}

        override fun onSpeechRecognizeComplete(
            p0: Boolean,
            p1: VoiceEventListener.ResultType?,
            p2: String?
        ) {}


        @RequiresApi(Build.VERSION_CODES.M)
        override fun onSpeech2TextComplete(p0: Boolean, p1: String?) {
            val result_string = VoiceResultJsonParser.parseVoiceResult(p1)
            Log.d("ListMusic Test", result_string)

            if (result_string.contains("退") || result_string.contains("關")) {
                musicPlayer.stop()
                startActivity(Intent(this@MusicPlayerActivity, ListMusicActivity::class.java))

            }

        }


        override fun onMixUnderstandComplete(
            p0: Boolean,
            p1: VoiceEventListener.ResultType?,
            p2: String?
        ) {}

        override fun onSpeechState(
            p0: VoiceEventListener.ListenType?,
            p1: VoiceEventListener.SpeechState?
        ) {}

        override fun onSpeakState(p0: VoiceEventListener.SpeakType?, p1: VoiceEventListener.SpeakState?) {}

        override fun onGrammarState(p0: Boolean, p1: String?) {}

        override fun onListenVolumeChanged(p0: VoiceEventListener.ListenType?, p1: Int) {}

        override fun onHotwordChange(
            p0: VoiceEventListener.HotwordState?,
            p1: VoiceEventListener.HotwordType?,
            p2: String?
        ) {}

    }

}