package com.example.vedioview

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.example.musicplayer.ListMusicActivity
import com.example.nuwaapplication.R
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.video_activity.*

class VideoPlayerActivity : AppCompatActivity() {

    lateinit var videoPlayer : MediaPlayer
    lateinit var video : Video

    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)

        //NUWA SETTING------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //------------------------------------------------------

        video = intent.getSerializableExtra("video") as Video


        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)


        videoView.setMediaController(mediaController)
        videoView.setVideoPath(video.getPath())
        videoView.requestFocus()
        videoView.start()
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
                1 -> startActivity(Intent(applicationContext, ListVideoActivity::class.java))
                2 -> mRobotAPI.startSpeech2Text(false)
            }
            Log.d("ListVideo Test", "on Long Press $p0")
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
            Log.d("ListVideo Test", result_string)

            if (result_string.contains("退") || result_string.contains("關")) {
                startActivity(Intent(this@VideoPlayerActivity, ListVideoActivity::class.java))

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