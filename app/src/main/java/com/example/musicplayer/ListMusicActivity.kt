package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_list_music.*
import com.example.nuwaapplication.R
import com.example.nuwaapplication.ui.ChatBoxActivity
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.alarm_main.*
import java.util.regex.Pattern

class ListMusicActivity : AppCompatActivity() {

    lateinit var songArrayList : ArrayList<Song>
    lateinit var songAdapter : SongAdapter
    lateinit var musicPlayer : MusicPlayerActivity

    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_music)

        //NUWA SETTING------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //------------------------------------------------------



        songArrayList = ArrayList()

        songAdapter = SongAdapter(this, songArrayList)

        lv_Songs.adapter = songAdapter



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 99)
            return
        } else {
            getSongs()
        }


        lv_Songs.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val song = songArrayList[position]
                val openMusicPlayer = Intent(this@ListMusicActivity, MusicPlayerActivity::class.java)
                openMusicPlayer.putExtra("song", song)
                startActivity(openMusicPlayer)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 99) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSongs()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("Recycle")
    private fun getSongs() {
        //read songs from phone
        val contentResolver = contentResolver
        val songUi = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val songCursor = contentResolver.query(songUi, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()) {

            val indexTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val indexArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val indexData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val title = songCursor.getString(indexTitle)
                val artist = songCursor.getString(indexArtist)
                val path = songCursor.getString(indexData)
                songArrayList.add(Song(title, artist, path))
            } while (songCursor.moveToNext())
        }
        songAdapter.notifyDataSetChanged()
    }

    val robotEventListener = object : RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)

            mRobotAPI.startTTS("可按住我的胸口輸入想播放的音樂")

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
                1 -> startActivity(Intent(applicationContext, ChatBoxActivity::class.java))
                2 -> mRobotAPI.startSpeech2Text(false)
                3 -> {mRobotAPI.startTTS("你好，這裡是音樂播放器")
                    mRobotAPI.motionPlay("666_BA_RHandL03", true)}
                4 -> {mRobotAPI.startTTS("想聽甚麼音樂可以直接點選或按住我的胸口跟我說喔!")
                    mRobotAPI.motionPlay("666_BA_RHandR03", true)}
                5 -> {mRobotAPI.startTTS("總共有${songArrayList.size}首歌在您的歌單中喔!")
                    mRobotAPI.motionPlay("666_BA_RHandS03", true)}
                6 -> {mRobotAPI.startTTS("播放您喜愛的音樂吧!")
                    mRobotAPI.motionPlay("666_BA_TurnL30", true)}
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

            if (result_string.contains("退")) {
                startActivity(Intent(this@ListMusicActivity, ChatBoxActivity::class.java))
            } else if (result_string == "") {
                mRobotAPI.startTTS("可按住我的胸口選擇播放的音樂")
            }else {
                for (i in 0 until songArrayList.size) {
                    if (songArrayList[i].getTitle().lowercase().contains(result_string)) {
                        val song = songArrayList[i]
                        val openMusicPlayer =
                            Intent(this@ListMusicActivity, MusicPlayerActivity::class.java)
                        openMusicPlayer.putExtra("song", song)
                        startActivity(openMusicPlayer)
                        break
                    }
                }
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

        override fun onSpeakState(p0: VoiceEventListener.SpeakType?, p1: VoiceEventListener.SpeakState?) {
            val random = (0..3).random()
            if (p1.toString() == "SPEAKING") {
                when (random) {
                    0 -> mRobotAPI.motionPlay("666_DA_Applaud", true)
                    1 -> mRobotAPI.motionPlay("666_DA_Bathe", true)
                    2 -> mRobotAPI.motionPlay("666_DA_Brushteeth", true)
                    3 -> mRobotAPI.motionPlay("666_DA_Catch", true)
                    else -> mRobotAPI.motionPlay("666_DA_WBPetted", true)
                }
                Log.d("Test", "$random")
            }
        }

        override fun onGrammarState(p0: Boolean, p1: String?) {}

        override fun onListenVolumeChanged(p0: VoiceEventListener.ListenType?, p1: Int) {}

        override fun onHotwordChange(
            p0: VoiceEventListener.HotwordState?,
            p1: VoiceEventListener.HotwordType?,
            p2: String?
        ) {}

    }

}