package com.example.alarmClock


import android.app.Activity
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*
import com.example.nuwaapplication.R
import com.example.nuwaapplication.ui.ChatBoxActivity
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import com.nuwarobotics.service.agent.RobotEventListener
import com.nuwarobotics.service.agent.VoiceEventListener
import com.nuwarobotics.service.agent.VoiceResultJsonParser
import kotlinx.android.synthetic.main.alarm_main.*
import java.text.NumberFormat
import java.util.regex.Pattern


class AlarmClockMain : AppCompatActivity() {

    lateinit var ring: Ringtone

    //NUWA API
    lateinit var mRobotAPI: NuwaRobotAPI
    lateinit var mClientId: IClientId


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_main)

        //NUWA SETTING------------------------------------------
        mClientId = IClientId(this.packageName)
        mRobotAPI = NuwaRobotAPI(this, mClientId)

        Log.d("Test", "register EventListener")
        mRobotAPI.registerRobotEventListener(robotEventListener)
        mRobotAPI.setListenParameter(VoiceEventListener.ListenType.RECOGNIZE, "language", "zh_tw")
        //------------------------------------------------------


        ring = RingtoneManager.getRingtone(
            this,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        )

            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (tv_time.text == AlarmTime() && sw_switch.isChecked) {
                        ring.play()
                        mRobotAPI.motionPlay("666_BA_ArmSCircle", true)
                        Log.d("Test", "Ring Play")
                    } else
                        ring.stop()
                        //mRobotAPI.motionStop(true)
                }

            }, 0, 1000)

    }


    override fun onDestroy() {
        super.onDestroy()
        sw_switch.isChecked = false
        Log.d("Test", "Successfully destroy")
    }

    fun AlarmTime(): String {

        var alarmHours = tp_timepicker.currentHour
        val alarmMinutes = tp_timepicker.currentMinute

        val stringAlarmMinutes: String

        if (alarmMinutes < 10)
            stringAlarmMinutes = "0$alarmMinutes"
        else
            stringAlarmMinutes = "$alarmMinutes"

        val stringAlarmTime: String

        if (alarmHours > 12) {
            alarmHours -= 12
            if (alarmHours < 10)
                stringAlarmTime = "下午$alarmHours:$stringAlarmMinutes"
            else stringAlarmTime = "下午$alarmHours:$stringAlarmMinutes"
        } else {
            stringAlarmTime = "上午$alarmHours:$stringAlarmMinutes"
        }
        return stringAlarmTime
    }


    val robotEventListener = object : RobotEventListener {
        override fun onWikiServiceStart() {
            Log.d("Test", "onWikiServiceStart, robot ready to be control")

            mRobotAPI.registerVoiceEventListener(voiceEventListener)
            mRobotAPI.requestSensor(NuwaRobotAPI.SENSOR_TOUCH or NuwaRobotAPI.SENSOR_PIR or NuwaRobotAPI.SENSOR_DROP)

            mRobotAPI.startTTS("現在時間為${tp_timepicker.currentHour}點${tp_timepicker.currentMinute}分")
            mRobotAPI.startTTS("請按住我的胸口，輸入您想設定的時間")
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
        override fun onTouchEvent(p0: Int, p1: Int) {Log.d("Alarm Test", "on Touch Event $p0, $p1")}
        override fun onPIREvent(p0: Int) {Log.d("Alarm Test", "on PIR Event $p0")}
        override fun onTap(p0: Int) {
            when (p0) {
                3 -> {tp_timepicker.hour += 1}
                4 -> {tp_timepicker.minute += 1}
            }
            Log.d("Alarm Test", "on Tap $p0")
        }
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onLongPress(p0: Int) {
            when (p0) {
                1 -> {
                    sw_switch.isChecked = false
                    startActivity(Intent(applicationContext, ChatBoxActivity::class.java))
                }
                2 -> mRobotAPI.startSpeech2Text(false)
                3 -> {tp_timepicker.hour -= 1}
                4 -> {tp_timepicker.minute -= 1}
                5 -> mRobotAPI.startTTS("我可以幫助提醒您時間到了!")
                6 -> {if (sw_switch.isChecked) {
                sw_switch.isChecked = false
                mRobotAPI.startTTS("以幫您關閉鬧鐘")
            }
            else {
                sw_switch.isChecked = true
                mRobotAPI.startTTS("以幫您開啟鬧鐘")
                    if (tp_timepicker.hour > 12) {
                        mRobotAPI.startTTS("設定下午${tp_timepicker.hour-12}點${tp_timepicker.minute}分")
                    } else if (tp_timepicker.hour == 12) {
                        mRobotAPI.startTTS("設定中午12點${tp_timepicker.minute}分")
                    } else mRobotAPI.startTTS("設定上午${tp_timepicker.hour}點${tp_timepicker.minute}分")
            }}
            }
            Log.d("Alarm Test", "on Long Press $p0")
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
            Log.d("Alarm Test", result_string)

            val pattern = Pattern.compile("\\d\\d點\\d\\d")

            if (result_string.contains("退") || result_string.contains("關")) {
                sw_switch.isChecked = false
                startActivity(Intent(this@AlarmClockMain, ChatBoxActivity::class.java))
            } else if (result_string.contains("關")) {
                sw_switch.isChecked = false
            } else if (pattern.matcher(result_string).matches()) {
                tp_timepicker.hour = result_string.substring(0, 2).toInt()
                tp_timepicker.minute = result_string.substring(3, 5).toInt()
                sw_switch.isChecked = true
            } else if (result_string.contains("開")){
                sw_switch.isChecked = true
            } else {
                mRobotAPI.startTTS("若要輸入請按住我的胸口")
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
            if (p1.toString() == "SPEAKING") {
                mRobotAPI.motionPlay("666_BA_ArmSCircle", true)
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
//https://www.youtube.com/watch?v=vJOW_Idnx7w