package com.example.weather

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.alarmClock.AlarmClockMain
import com.example.musicplayer.ListMusicActivity
import com.example.nuwaapplication.R
import com.example.nuwaapplication.ui.ChatBoxActivity
import com.example.translator.Language
import com.example.translator.TranslateAPI
import com.example.vedioview.ListVideoActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.*
import kotlinx.android.synthetic.main.weather_activity.*
import java.util.*
import kotlin.collections.ArrayList

class WeatherMain : AppCompatActivity() {

    private val API_KEY = "ca0e2a321124cebea6d8520ba7323cdf"

    lateinit var btnSerch: Button
    lateinit var etCityName: EditText
    lateinit var iconWeather: ImageView
    lateinit var tvTemp: TextView
    lateinit var tvCity: TextView
    lateinit var lvDailyWeather: ListView

    //Nuwa setting-----------------------
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId
    //------------------------------------


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        btnSerch = findViewById(R.id.btnSearch)
        etCityName = findViewById(R.id.etCityName)
        iconWeather = findViewById(R.id.iconWeather)
        tvTemp = findViewById(R.id.tvTemp)
        tvCity = findViewById(R.id.tvCity)
        lvDailyWeather = findViewById(R.id.lvDailyWeather)

        //Nuwa API--------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //-----------------------------------------------------


        btnSerch.setOnClickListener {
            val city = etCityName.text.toString()

            if (city.isEmpty()) {
                Toast.makeText(this, "請輸入城市名稱", Toast.LENGTH_SHORT).show()
            } else {
                loadWeatherByCityName(city)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadWeatherByCityName(city: String) {
        Ion.with(this)
            .load("http://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$API_KEY")
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                } else {
                    val main: JsonObject = result.get("main").asJsonObject
                    val temp: Double = main.get("temp").asDouble
                    tvTemp.text = "$temp℃"

                    val sys: JsonObject = result.get("sys").asJsonObject
                    val country: String = sys.get("country").asString
                    tvCity.text = "$city, $country"

                    val status = result.get("weather").asJsonArray
                    val description = status.get(0).asJsonObject.get("description").asString


                    //Nuwa TTS-------------------------------------------------------
                    val translateAPI = TranslateAPI(Language.AUTO_DETECT, Language.CHINESE_TRADITIONAL, "today's weather in $city city is $description in $temp degree")
                    translateAPI.setTranslateListener(object : TranslateAPI.TranslateListener {
                        override fun onSuccess(translatedText: String?) {
                            mRobotAPI.startTTS(translatedText)
                        }
                        override fun onFailure(ErrorText: String?) {
                            Log.d("Error", "Error")
                        }
                    })
                    //---------------------------------------------------------------

                    val weather = result.get("weather").asJsonArray
                    val icon = weather.get(0).asJsonObject.get("icon").asString
                    loadIcon(icon)


                    val coord: JsonObject = result.get("coord").asJsonObject
                    val lon = coord.get("lon").asDouble
                    val lat = coord.get("lat").asDouble
                    loadDailyForecast(lon, lat)
                }
            }
    }

    private fun loadDailyForecast(lon: Double, lat: Double) {
        Ion.with(this)
            .load("https://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&exclude=hourly,minutely&units=metric&appid=$API_KEY")
            .asJsonObject()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
                } else {
                    val weatherList = ArrayList<Weather>()
                    val timeZone: String = result.get("timezone").asString
                    val daily: JsonArray = result.get("daily").asJsonArray
                    val size: Int = daily.size()
                    for (i in 1..size) {
                        val date = daily.get(i - 1).asJsonObject.get("dt").asLong
                        val temp =
                            daily.get(i - 1).asJsonObject.get("temp").asJsonObject.get("day").asDouble
                        val temptts =
                            daily.get(1).asJsonObject.get("temp").asJsonObject.get("day").asDouble
                        val icon =
                            daily.get(i - 1).asJsonObject.get("weather").asJsonArray.get(0).asJsonObject.get(
                                "icon"
                            ).asString
                        val description =
                            daily.get(1).asJsonObject.get("weather").asJsonArray.get(0).asJsonObject.get(
                                "description"
                            ).asString
                        weatherList.add(Weather(date, timeZone, temp, icon))


                        if (i == size) {
                            //Nuwa TTS-----------------------------------------------
                            val translateAPI = TranslateAPI(Language.AUTO_DETECT, Language.CHINESE_TRADITIONAL, "And tomorrow's weather is $description $temptts degree")
                            translateAPI.setTranslateListener(object : TranslateAPI.TranslateListener {
                                override fun onSuccess(translatedText: String?) {
                                    mRobotAPI.startTTS(translatedText)
                                }
                                override fun onFailure(ErrorText: String?) {
                                    Log.d("Error", "Error")
                                }

                            })
                            //-------------------------------------------------------
                        }
                    }

                    val dailyWeatherAdapter = DailyWeatherAdapter(this, weatherList)
                    lvDailyWeather.adapter = dailyWeatherAdapter


                }
            }
    }


    private fun loadIcon(icon: String) {
        Ion.with(this)
            .load("http://openweathermap.org/img/w/$icon.png")
            .intoImageView(iconWeather)
    }


    //Nuwa---------------------------------------------------------


    val robotEventListener = object: RobotEventListener {

        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)

            loadWeatherByCityName("Taipei")


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
                3 -> {mRobotAPI.startTTS("你好，我是KEBBI天氣播報員")
                mRobotAPI.motionPlay("666_BA_RHandL03", true)}
                4 -> {mRobotAPI.startTTS("今天一整天都要有活力喔!")
                    mRobotAPI.motionPlay("666_BA_RHandR03", true)}
                5 -> {mRobotAPI.startTTS("我可以找出世界各地的天氣狀況!")
                    mRobotAPI.motionPlay("666_BA_RHandS03", true)}
                6 -> {mRobotAPI.startTTS("KEBBI播報員在此為您服務!")
                    mRobotAPI.motionPlay("666_BA_TurnL30", true)}
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

    val voiceEventListener = object: VoiceEventListener {
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
            val translateAPI = TranslateAPI(Language.AUTO_DETECT, Language.ENGLISH, result_string)

            if (result_string.contains("退") || result_string.contains("關")) {
                startActivity(Intent(this@WeatherMain, ChatBoxActivity::class.java))
            } else {
                translateAPI.setTranslateListener(object : TranslateAPI.TranslateListener {
                    override fun onSuccess(translatedText: String?) {
                        if (translatedText != null) {
                            loadWeatherByCityName(translatedText)
                        }
                    }

                    override fun onFailure(ErrorText: String?) {
                        Log.d("Error", "Error")
                    }
                })
            }
        }

        override fun onMixUnderstandComplete(p0: Boolean, p1: VoiceEventListener.ResultType?, p2: String) {


        }

        override fun onSpeechState(p0: VoiceEventListener.ListenType?, p1: VoiceEventListener.SpeechState?) {

        }

        override fun onSpeakState(p0: VoiceEventListener.SpeakType?, p1: VoiceEventListener.SpeakState?) {
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

