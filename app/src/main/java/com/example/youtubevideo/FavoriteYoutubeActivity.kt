package com.example.youtubevideo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.example.nuwaapplication.R
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser

class FavoriteYoutubeActivity : AppCompatActivity() {

    lateinit var youtubePlayer: YouTubePlayerView

    //Nuwa setting-----------------------
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId
    //------------------------------------

    lateinit var id: String
    private lateinit var title: String
    lateinit var author: String
    lateinit var youtubeID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_youtube)

        //Nuwa API--------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //-----------------------------------------------------

        youtubePlayer = findViewById(R.id.youtube_player)

        //Get Data form Intent
        getIntentData()
        //Set Title
        supportActionBar?.title = title

        lifecycle.addObserver(youtubePlayer)

        if (intent.hasExtra("pages")) {
            youtubeID = intent.getStringExtra("pages").toString()

        } else {
            Toast.makeText(this@FavoriteYoutubeActivity, "Error Youtube ID", Toast.LENGTH_SHORT).show()
        }

        youtubePlayer.addYouTubePlayerListener(object: AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                youTubePlayer.loadVideo(youtubeID, 0f)
            }
        })
    }

    private fun getIntentData() {
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()
        author = intent.getStringExtra("author").toString()
        youtubeID = intent.getStringExtra("pages").toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        youtubePlayer.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.data_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.video_data) {
            val intent = Intent(this, UpdateActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("title", title)
            intent.putExtra("author", author)
            intent.putExtra("pages", youtubeID)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    //Nuwa---------------------------------------------------------


    val robotEventListener = object : RobotEventListener {

        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)


        }

        override fun onWikiServiceStop() {

        }

        override fun onWikiServiceCrash() {

        }

        override fun onWikiServiceRecovery() {

        }

        override fun onStartOfMotionPlay(p0: String?) {

        }

        override fun onPauseOfMotionPlay(p0: String?) {

        }

        override fun onStopOfMotionPlay(p0: String?) {

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

        }

        override fun onPIREvent(p0: Int) {

        }

        override fun onTap(p0: Int) {

        }

        override fun onLongPress(p0: Int) {
            when (p0) {
                1 -> startActivity(Intent(applicationContext, FavoriteActivity::class.java))
                2 -> mRobotAPI.startSpeech2Text(false)
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

        }

        override fun onSpeechRecognizeComplete(
            p0: Boolean,
            p1: VoiceEventListener.ResultType?,
            p2: String?
        ) {

        }

        override fun onSpeech2TextComplete(p0: Boolean, p1: String?) {
            val result_string = VoiceResultJsonParser.parseVoiceResult(p1)

            if (result_string.contains("退") || result_string.contains("關")) {
                startActivity(Intent(applicationContext, FavoriteActivity::class.java))
            }

        }

        override fun onMixUnderstandComplete(
            p0: Boolean,
            p1: VoiceEventListener.ResultType?,
            p2: String
        ) {


        }

        override fun onSpeechState(
            p0: VoiceEventListener.ListenType?,
            p1: VoiceEventListener.SpeechState?
        ) {

        }

        override fun onSpeakState(
            p0: VoiceEventListener.SpeakType?,
            p1: VoiceEventListener.SpeakState?
        ) {
            val random = (0..3).random()
            if (p1.toString() == "SPEAKING") {
                when (random) {
                    0 -> mRobotAPI.motionPlay("666_DA_Hit", true)
                    1 -> mRobotAPI.motionPlay("666_EM_Curse", true)
                    2 -> mRobotAPI.motionPlay("666_DA_Phone", true)
                    3 -> mRobotAPI.motionPlay("666_EM_Disgusted03", true)
                    else -> mRobotAPI.motionPlay("666_DA_WBPetted", true)
                }
                Log.d("Test", "$random")
            }
        }

        override fun onGrammarState(p0: Boolean, p1: String) {
            Log.d("Test", "GrammerState $p1")
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
    //-----------------------------------------------------------------------------

}