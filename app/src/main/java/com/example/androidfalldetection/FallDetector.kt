package com.example.androidfalldetection

import android.graphics.Color
import android.hardware.SensorManager
import android.os.Handler
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*


class FallDetector {

    var running:Boolean = true
    var acceleration_g = SensorManager.GRAVITY_EARTH
    var landingFlag:Boolean = false
    var layingOnGroundFlag:Boolean = false
    var startTime:Long = 0
    var textView: TextView? = null
    var fallCountView: TextView? = null
    var fallCount = 0;

    fun detectFall(ad: AccelerometerData, text: TextView, fall_count:TextView) {
        val acc = ad.countAcceleration()
        textView = text
        fallCountView = fall_count

        acceleration_g = acc / SensorManager.GRAVITY_EARTH;

        //PHASE ONE
        //In a free fall the x,y,z values of the accelerometer are near zero.
        if(acceleration_g < 0.3) // Free fall
        {

            if (!running) {
                running = true
            }
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);

        }
    }

//    fun resetVariables() {
//        layingOnGroundFlag = false
//        landingFlag = false
//        running = false
//    }

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
            if (seconds <= 3 && landingFlag == true) {
                if (acceleration_g >= 0.9 && acceleration_g <= 1.1) {
                    layingOnGroundFlag = true
                } else {
//                    resetVariables()
                }
//                return
            }
            if (layingOnGroundFlag == true && seconds > 3) {
                var ms = millis.toString()
                textView!!.text = "UPADEK po $ms ms, watek ${Thread.currentThread().getName()}, ${Thread.currentThread().getId()}\n"
                fallCount += 1
                fallCountView!!.text = fallCount.toString()
                layingOnGroundFlag = false
            } else if (seconds > 3 && landingFlag == false) {
                //reset variables??
                running = false
            }
            timerHandler.postDelayed(this, 500)
        }
    }

}