package com.example.youtubevideo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MusicPlayerActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.ArrayList
import com.example.nuwaapplication.R
import com.example.nuwaapplication.ui.ChatBoxActivity
import com.example.translator.Language
import com.example.translator.TranslateAPI
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.alarm_main.*

class YoututbevideoMainActivity : AppCompatActivity() {

    //Nuwa setting-----------------------
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId
    //------------------------------------

    private lateinit var recyclerView: RecyclerView
    private lateinit var add_button: FloatingActionButton
    private lateinit var empty_imageview: ImageView
    private lateinit var no_data: TextView

    private val myDB = MyDatabaseHelper(this)
    private var book_id = ArrayList<String>()
    private var book_title = ArrayList<String>()
    private var book_author = ArrayList<String>()
    private var book_pages = ArrayList<String>()
    private val customAdapter =
        CustomAdapter(this, this, book_id, book_title, book_author, book_pages)

    //sideable character
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtubevideo_main)

        //Nuwa API--------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //-----------------------------------------------------

        recyclerView = findViewById(R.id.recyclerView)
        add_button = findViewById(R.id.floatingActionButton)

        empty_imageview = findViewById(R.id.empty_imageview)
        no_data = findViewById(R.id.no_data)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        sideableMenuAction()

        add_button.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        storeDataInArrays()

        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun sideableMenuAction() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.favorite -> startActivity(Intent(this, FavoriteActivity::class.java))
                //R.id.home -> startActivity(Intent(this, MainActivity::class.java))
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            recreate()
        }
    }

    private fun storeDataInArrays() {
        val cursor = myDB.readAllData()
        if (cursor?.count == 0) {
            empty_imageview.visibility = View.VISIBLE
            no_data.visibility = View.VISIBLE
        } else {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    book_id.add(cursor.getString(0))
                    book_title.add(cursor.getString(1))
                    book_author.add(cursor.getString(2))
                    book_pages.add(cursor.getString(3))
                    empty_imageview.visibility = View.GONE
                    no_data.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.youtube_menu, menu)
        inflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.delete_all) {
            confirmDialog()
        }
        if (item.itemId == R.id.youtube) {
            val site = Intent(Intent.ACTION_VIEW)
            site.data = Uri.parse("https://www.youtube.com/")
            startActivity(site)
        }
        return super.onOptionsItemSelected(item)
    }


    fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete All?")
        builder.setMessage("Are you sure you want to delete all Data?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show()
            val myDB = MyDatabaseHelper(this)
            myDB.deleteAllData()
            //Refresh Activity
            val intent = Intent(this, YoututbevideoMainActivity::class.java)
            startActivity(intent)
            finish()
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->

        })
        builder.create().show()
    }
    //Nuwa---------------------------------------------------------


    val robotEventListener = object : RobotEventListener {

        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)

            mRobotAPI.startTTS("你好，這裡是Youtube影片播放清單")

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
                1 -> startActivity(Intent(applicationContext, ChatBoxActivity::class.java))
                2 -> mRobotAPI.startSpeech2Text(false)
                3 -> {mRobotAPI.startTTS("總共有${book_id.size}首歌在您的歌單中喔!")
                    mRobotAPI.motionPlay("666_BA_RHandS03", true)}
                4 -> startActivity(Intent(applicationContext, AddActivity::class.java))
                5 -> {
                    val site = Intent(Intent.ACTION_VIEW)
                    site.data = Uri.parse("https://www.youtube.com/")
                    startActivity(site)}
                6 -> {startActivity(Intent(applicationContext, FavoriteActivity::class.java))}
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
                startActivity(Intent(applicationContext, ChatBoxActivity::class.java))
            } else if (result_string.contains("線上")) {
                val site = Intent(Intent.ACTION_VIEW)
                site.data = Uri.parse("https://www.youtube.com/")
                startActivity(site)
            } else if (result_string.contains("增")) {
                val intent = Intent(applicationContext, AddActivity::class.java)
                startActivity(intent)
            } else {
                for (i in 0 until book_id.size) {
                    if (book_title[i].lowercase().contains(result_string)) {
                        val intent = Intent(applicationContext, YoutubeActivity::class.java)
                        intent.putExtra("id", book_id[i])
                        intent.putExtra("title", book_title[i])
                        intent.putExtra("author", book_author[i])
                        intent.putExtra("pages", book_pages[i])
                        startActivityForResult(intent, 1)
                        break
                    }
                }
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