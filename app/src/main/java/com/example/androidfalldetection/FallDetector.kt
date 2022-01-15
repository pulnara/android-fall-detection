package com.example.androidfalldetection

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.hardware.SensorManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment


class FallDetector : Fragment(R.layout.fragment_fall_detection) {

    var running:Boolean = true
    var acceleration_g = SensorManager.GRAVITY_EARTH
    var landingFlag:Boolean = false
    var layingOnGroundFlag:Boolean = false
    var startTime:Long = 0
    var fallCount = 0
    lateinit var fallDetectionView: View
    private lateinit var abortButton: Button
    private lateinit var smsManager: SmsManager
    var smsSent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        smsManager = SmsManager.getDefault()
        fallDetectionView = inflater.inflate(R.layout.fragment_fall_detection, container, false)
        abortButton = fallDetectionView.findViewById(R.id.abortButton)
        abortButton.setOnClickListener {
            abortButton.visibility = View.INVISIBLE
            fallDetectionView.background = null
            resetVariables()
        }
        return fallDetectionView
    }

    fun detectFall(ad: AccelerometerData) {
        val acc = ad.countAcceleration()
        acceleration_g = acc / SensorManager.GRAVITY_EARTH

        //PHASE ONE
        //In a free fall the x,y,z values of the accelerometer are near zero.
        if(acceleration_g < 0.3) // Free fall
        {

            if (!running) {
                running = true
            }
            startTime = System.currentTimeMillis()
            timerHandler.postDelayed(timerRunnable, 0)

        }
    }

    fun resetVariables() {
        layingOnGroundFlag = false
        landingFlag = false
        running = false
    }

    fun addBackgroundAnimation() {
        val drawable = AnimationDrawable()
        val handler = Handler()

        drawable.addFrame(ColorDrawable(Color.RED), 400)
        drawable.addFrame(ColorDrawable(Color.GREEN), 400)
        drawable.setOneShot(false)

        fallDetectionView.setBackground(drawable)
        handler.postDelayed( { drawable.start() }, 100)
    }

    fun playNotificationSound() {
        val alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()
    }

    //Runs without a timer by reposting this handler at the end of the runnable.
    var timerHandler: Handler = Handler()
    var timerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!running) return
            val millis = System.currentTimeMillis() - startTime
            var seconds = (millis / 1000).toInt()
            seconds = seconds % 60

            //PHASE TWO
            //A fall generally occurs in a short period of 0.4 - 0.8s.
            //A landing must happen within a second after a free fall has been detected.
            //If the vector sum raises to a value over 30m/s, the phone/person has landed.
            if (seconds <= 1 && acceleration_g > 2)
            {
                landingFlag = true
            }
            //PHASE THREE
            //If the phone is not moving for two seconds after the landing, the user/phone is laying on the ground.
            if (seconds > 2 && seconds <= 3 && landingFlag == true && acceleration_g >= 0.9 && acceleration_g <= 1.1) {
                    layingOnGroundFlag = true
            }
            if (layingOnGroundFlag == true) {
                fallCount += 1
                layingOnGroundFlag = false
                abortButton.visibility = View.VISIBLE
                addBackgroundAnimation()
                playNotificationSound()
            }else if (seconds > 3 && landingFlag == false) {
                //reset variables??
                running = false
            } else if ( seconds > 10 && landingFlag == true && !smsSent){
                sendSms()
                smsSent = true
            }
            timerHandler.postDelayed(this, 500)
        }
    }

    private fun sendSms() {
        val applicationContext = requireContext()
        val sharedPreferences = applicationContext.getSharedPreferences(
            Constants.AppName,
            Context.MODE_PRIVATE
        )
        val phoneNumber = sharedPreferences.getString(Constants.PhoneNumber, null)

        phoneNumber?.also { number ->
            val intent = Intent(applicationContext, FallDetector::class.java)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

            smsManager.sendTextMessage(
                number,
                null,
                Constants.FallDetectedMessage,
                pendingIntent,
                null
            )
        }
    }

}